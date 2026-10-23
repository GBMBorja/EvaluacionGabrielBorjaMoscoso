package com.evaluacion.gabrielborjamoscoso.service.impl;

import com.evaluacion.gabrielborjamoscoso.dto.request.TransferenciaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.PageResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.TransferenciaResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;
import com.evaluacion.gabrielborjamoscoso.entity.Transferencia;
import com.evaluacion.gabrielborjamoscoso.exception.BusinessException;
import com.evaluacion.gabrielborjamoscoso.exception.ResourceNotFoundException;
import com.evaluacion.gabrielborjamoscoso.mapper.TransferenciaMapper;
import com.evaluacion.gabrielborjamoscoso.repository.CuentaRepository;
import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.DeteccionSospechosaService;
import com.evaluacion.gabrielborjamoscoso.service.LimiteDiarioService;
import com.evaluacion.gabrielborjamoscoso.service.TransferenciaService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TransferenciaServiceImpl implements TransferenciaService {

    private static final Logger LOG = Logger.getLogger(TransferenciaServiceImpl.class);

    @Inject
    CuentaRepository cuentaRepository;

    @Inject
    TransferenciaRepository transferenciaRepository;

    @Inject
    DeteccionSospechosaService deteccionSospechosaService;

    @Inject
    LimiteDiarioService limiteDiarioService;

    @Inject
    TransferenciaMapper transferenciaMapper;

    @Override
    @Transactional
    public TransferenciaResponseDto ejecutarTransferencia(TransferenciaRequestDto requestDto) {
        LOG.infof("Iniciando transferencia de $%s desde cuenta %s hacia cuenta %s",
                requestDto.getMonto(), requestDto.getNumeroCuentaOrigen(), requestDto.getNumeroCuentaDestino());

        // 1. Validar que cuentas origen y destino sean distintas
        if (requestDto.getNumeroCuentaOrigen().trim().equals(requestDto.getNumeroCuentaDestino().trim())) {
            throw new BusinessException("La cuenta de origen no puede ser igual a la cuenta de destino", 400);
        }

        // 2. Validar que el monto sea mayor a 0
        if (requestDto.getMonto() == null || requestDto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto a transferir debe ser mayor a cero", 400);
        }

        // 3. Validar existencia de la cuenta origen
        Cuenta cuentaOrigen = cuentaRepository.findByNumeroCuenta(requestDto.getNumeroCuentaOrigen().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cuenta de origen '" + requestDto.getNumeroCuentaOrigen() + "' no existe"));

        // 4. Validar existencia de la cuenta destino
        Cuenta cuentaDestino = cuentaRepository.findByNumeroCuenta(requestDto.getNumeroCuentaDestino().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cuenta de destino '" + requestDto.getNumeroCuentaDestino() + "' no existe"));

        // 5. Validar estado ACTIVA de ambas cuentas
        if (cuentaOrigen.getEstado() != EstadoCuenta.ACTIVA) {
            throw new BusinessException(
                    "La cuenta de origen (" + cuentaOrigen.getNumeroCuenta() + ") no está activa para operar", 422);
        }
        if (cuentaDestino.getEstado() != EstadoCuenta.ACTIVA) {
            throw new BusinessException(
                    "La cuenta de destino (" + cuentaDestino.getNumeroCuenta() + ") no está activa para recibir fondos", 422);
        }

        // 6. Validar saldo suficiente
        if (cuentaOrigen.getSaldo().compareTo(requestDto.getMonto()) < 0) {
            throw new BusinessException(
                    String.format("Saldo insuficiente en la cuenta de origen. Saldo actual: $%.2f, Monto requerido: $%.2f",
                            cuentaOrigen.getSaldo(), requestDto.getMonto()), 422);
        }

        // 7. CA-04: Control de límite diario acumulado de $10,000 en día calendario
        limiteDiarioService.validarLimiteDiario(cuentaOrigen.getId(), requestDto.getMonto());

        // 8. CA-03: Detección de operaciones sospechosas
        DeteccionSospechosaService.ResultadoSospecha resultadoSospecha =
                deteccionSospechosaService.evaluarSospecha(cuentaOrigen.getId(), requestDto.getMonto());

        EstadoTransferencia estado = resultadoSospecha.sospechosa()
                ? EstadoTransferencia.REQUIERE_REVISION
                : EstadoTransferencia.COMPLETADA;

        // 9. Operación atómica de actualización de saldos
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(requestDto.getMonto()));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(requestDto.getMonto()));

        cuentaRepository.persist(cuentaOrigen);
        cuentaRepository.persist(cuentaDestino);

        // 10. Persistir la transferencia
        Transferencia transferencia = Transferencia.builder()
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .monto(requestDto.getMonto())
                .fecha(LocalDateTime.now())
                .estado(estado)
                .sospechosa(resultadoSospecha.sospechosa())
                .motivoSospecha(resultadoSospecha.motivo())
                .build();

        transferenciaRepository.persist(transferencia);

        LOG.infof("Transferencia procesada con éxito (ID: %d, Estado: %s, Sospechosa: %s)",
                transferencia.getId(), transferencia.getEstado(), transferencia.isSospechosa());

        return transferenciaMapper.toDto(transferencia);
    }

    @Override
    public List<TransferenciaResponseDto> listarTransferencias(
            String cuentaOrigen,
            String cuentaDestino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoTransferencia estado) {

        PanacheQuery<Transferencia> query = transferenciaRepository.findFiltered(
                cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado);
        return transferenciaMapper.toDtoList(query.list());
    }

    @Override
    public PageResponseDto<TransferenciaResponseDto> listarTransferenciasPaginadas(
            String cuentaOrigen,
            String cuentaDestino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoTransferencia estado,
            int pagina,
            int tamano) {

        PanacheQuery<Transferencia> query = transferenciaRepository.findFiltered(
                cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado);
        query.page(Page.of(pagina, tamano));

        List<TransferenciaResponseDto> contenido = transferenciaMapper.toDtoList(query.list());

        return PageResponseDto.<TransferenciaResponseDto>builder()
                .contenido(contenido)
                .pagina(pagina)
                .tamano(tamano)
                .totalElementos(query.count())
                .totalPaginas(query.pageCount())
                .build();
    }
}
