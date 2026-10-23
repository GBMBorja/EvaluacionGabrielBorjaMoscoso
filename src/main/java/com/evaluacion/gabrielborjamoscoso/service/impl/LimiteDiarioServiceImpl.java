package com.evaluacion.gabrielborjamoscoso.service.impl;

import com.evaluacion.gabrielborjamoscoso.exception.BusinessException;
import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.LimiteDiarioService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ApplicationScoped
public class LimiteDiarioServiceImpl implements LimiteDiarioService {

    private static final Logger LOG = Logger.getLogger(LimiteDiarioServiceImpl.class);
    public static final BigDecimal LIMITE_DIARIO_MAXIMO = new BigDecimal("10000.00");

    @Inject
    TransferenciaRepository transferenciaRepository;

    @Override
    public void validarLimiteDiario(Long cuentaOrigenId, BigDecimal monto) {
        BigDecimal acumuladoHoy = obtenerAcumuladoHoy(cuentaOrigenId);
        BigDecimal nuevoAcumulado = acumuladoHoy.add(monto);

        if (nuevoAcumulado.compareTo(LIMITE_DIARIO_MAXIMO) > 0) {
            BigDecimal disponible = obtenerDisponibleHoy(cuentaOrigenId);
            String mensaje = String.format(
                    java.util.Locale.US,
                    "Límite diario de transferencias superado. Límite máximo: $%.2f. Acumulado transferido hoy: $%.2f. Monto restante disponible para transferir hoy: $%.2f.",
                    LIMITE_DIARIO_MAXIMO, acumuladoHoy, disponible
            );
            LOG.warnf("Operación rechazada para cuenta ID %d: %s", cuentaOrigenId, mensaje);
            throw new BusinessException(mensaje, 422);
        }
    }

    @Override
    public BigDecimal obtenerAcumuladoHoy(Long cuentaOrigenId) {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);
        return transferenciaRepository.sumTransferredToday(cuentaOrigenId, inicioDia, finDia);
    }

    @Override
    public BigDecimal obtenerDisponibleHoy(Long cuentaOrigenId) {
        BigDecimal acumulado = obtenerAcumuladoHoy(cuentaOrigenId);
        BigDecimal disponible = LIMITE_DIARIO_MAXIMO.subtract(acumulado);
        return disponible.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : disponible;
    }
}
