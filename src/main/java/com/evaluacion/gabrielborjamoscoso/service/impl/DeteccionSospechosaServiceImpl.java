package com.evaluacion.gabrielborjamoscoso.service.impl;

import com.evaluacion.gabrielborjamoscoso.repository.TransferenciaRepository;
import com.evaluacion.gabrielborjamoscoso.service.DeteccionSospechosaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DeteccionSospechosaServiceImpl implements DeteccionSospechosaService {

    private static final Logger LOG = Logger.getLogger(DeteccionSospechosaServiceImpl.class);
    private static final BigDecimal UMBRAL_MONTO_SOSPECHOSO = new BigDecimal("5000.00");
    private static final long MAX_TRANSFERENCIAS_HORA = 5;

    @Inject
    TransferenciaRepository transferenciaRepository;

    @Override
    public ResultadoSospecha evaluarSospecha(Long cuentaOrigenId, BigDecimal monto) {
        List<String> motivos = new ArrayList<>();

        // Condición A: monto individual supera $5,000
        if (monto != null && monto.compareTo(UMBRAL_MONTO_SOSPECHOSO) > 0) {
            motivos.add(String.format("El monto individual ($%s) supera el umbral permitido de $5,000.00", monto));
        }

        // Condición B: cuenta origen acumula más de 5 transferencias en la última hora
        LocalDateTime unaHoraAtras = LocalDateTime.now().minusHours(1);
        long conteoHora = transferenciaRepository.countTransfersInLastHour(cuentaOrigenId, unaHoraAtras);
        if (conteoHora >= MAX_TRANSFERENCIAS_HORA) {
            motivos.add(String.format("La cuenta origen acumula %d transferencias en la última hora (límite: 5)", conteoHora));
        }

        if (!motivos.isEmpty()) {
            String motivoFinal = String.join(" | ", motivos);
            LOG.warnf("Alerta de operación sospechosa detectada en cuenta ID %d: %s", cuentaOrigenId, motivoFinal);
            return new ResultadoSospecha(true, motivoFinal);
        }

        return new ResultadoSospecha(false, null);
    }
}
