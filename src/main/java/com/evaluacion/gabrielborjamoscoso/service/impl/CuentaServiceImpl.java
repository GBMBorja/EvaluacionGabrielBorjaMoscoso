package com.evaluacion.gabrielborjamoscoso.service.impl;

import com.evaluacion.gabrielborjamoscoso.dto.request.CuentaRequestDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResponseDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.CuentaResumenDto;
import com.evaluacion.gabrielborjamoscoso.dto.response.PageResponseDto;
import com.evaluacion.gabrielborjamoscoso.entity.Cuenta;
import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.exception.BusinessException;
import com.evaluacion.gabrielborjamoscoso.exception.ResourceNotFoundException;
import com.evaluacion.gabrielborjamoscoso.mapper.CuentaMapper;
import com.evaluacion.gabrielborjamoscoso.repository.CuentaRepository;
import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.CuentaService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class CuentaServiceImpl implements CuentaService {

    private static final Logger LOG = Logger.getLogger(CuentaServiceImpl.class);

    @Inject
    CuentaRepository cuentaRepository;

    @Inject
    TransferenciaRepository transferenciaRepository;

    @Inject
    CuentaMapper cuentaMapper;

    @Override
    @Transactional
    public CuentaResponseDto registrarCuenta(CuentaRequestDto requestDto) {
        LOG.infof("Registrando nueva cuenta: %s para titular: %s", requestDto.getNumeroCuenta(), requestDto.getTitular());

        if (cuentaRepository.existsByNumeroCuenta(requestDto.getNumeroCuenta())) {
            throw new BusinessException(
                    "Ya existe una cuenta bancaria registrada con el número: " + requestDto.getNumeroCuenta(),
                    400
            );
        }

        if (requestDto.getSaldoInicial() != null && requestDto.getSaldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El saldo inicial no puede ser negativo", 400);
        }

        Cuenta cuenta = cuentaMapper.toEntity(requestDto);
        if (cuenta.getEstado() == null) {
            cuenta.setEstado(EstadoCuenta.ACTIVA);
        }

        cuentaRepository.persist(cuenta);
        LOG.infof("Cuenta registrada con éxito con ID: %d", cuenta.getId());
        return cuentaMapper.toDto(cuenta);
    }

    @Override
    public List<CuentaResponseDto> listarCuentas() {
        List<Cuenta> cuentas = cuentaRepository.listAll(Sort.by("id"));
        return cuentaMapper.toDtoList(cuentas);
    }

    @Override
    public PageResponseDto<CuentaResponseDto> listarCuentasPaginadas(int pagina, int tamano) {
        PanacheQuery<Cuenta> query = cuentaRepository.findAll(Sort.by("id")).page(Page.of(pagina, tamano));
        List<CuentaResponseDto> contenido = cuentaMapper.toDtoList(query.list());

        return PageResponseDto.<CuentaResponseDto>builder()
                .contenido(contenido)
                .pagina(pagina)
                .tamano(tamano)
                .totalElementos(query.count())
                .totalPaginas(query.pageCount())
                .build();
    }

    @Override
    public Cuenta obtenerEntidadPorId(Long id) {
        return cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cuenta con ID: " + id));
    }

    @Override
    public Cuenta obtenerEntidadPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cuenta con número: " + numeroCuenta));
    }

    @Override
    public CuentaResumenDto obtenerResumen(Long id) {
        Cuenta cuenta = obtenerEntidadPorId(id);

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalHoy = transferenciaRepository.sumTransferredToday(id, inicioDia, finDia);
        long conteoHoy = transferenciaRepository.countTransfersToday(id, inicioDia, finDia);
        long sospechosas = transferenciaRepository.countSuspiciousTransfers(id);

        return CuentaResumenDto.builder()
                .cuentaId(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .titular(cuenta.getTitular())
                .saldoActual(cuenta.getSaldo())
                .totalTransferidoHoy(totalHoy)
                .cantidadTransferenciasHoy(conteoHoy)
                .cantidadSospechosas(sospechosas)
                .build();
    }
}
