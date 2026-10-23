package com.evaluacion.gabrielborjamoscoso.repository;

import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;
import com.evaluacion.gabrielborjamoscoso.entity.Transferencia;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TransferenciaRepository implements PanacheRepository<Transferencia> {

    public long countTransfersInLastHour(Long cuentaOrigenId, LocalDateTime oneHourAgo) {
        return count("cuentaOrigen.id = :cuentaId and fecha >= :oneHourAgo and estado != :rechazada",
                Parameters.with("cuentaId", cuentaOrigenId)
                        .and("oneHourAgo", oneHourAgo)
                        .and("rechazada", EstadoTransferencia.RECHAZADA));
    }

    public BigDecimal sumTransferredToday(Long cuentaOrigenId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Object result = getEntityManager().createQuery(
                "select coalesce(sum(t.monto), 0) from Transferencia t where t.cuentaOrigen.id = :cuentaId and t.fecha >= :start and t.fecha <= :end and t.estado != :rechazada")
                .setParameter("cuentaId", cuentaOrigenId)
                .setParameter("start", startOfDay)
                .setParameter("end", endOfDay)
                .setParameter("rechazada", EstadoTransferencia.RECHAZADA)
                .getSingleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    public long countTransfersToday(Long cuentaOrigenId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return count("cuentaOrigen.id = :cuentaId and fecha >= :start and fecha <= :end and estado != :rechazada",
                Parameters.with("cuentaId", cuentaOrigenId)
                        .and("start", startOfDay)
                        .and("end", endOfDay)
                        .and("rechazada", EstadoTransferencia.RECHAZADA));
    }

    public long countSuspiciousTransfers(Long cuentaOrigenId) {
        return count("cuentaOrigen.id = :cuentaId and sospechosa = true",
                Parameters.with("cuentaId", cuentaOrigenId));
    }

    public PanacheQuery<Transferencia> findFiltered(
            String cuentaOrigen,
            String cuentaDestino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoTransferencia estado) {

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (cuentaOrigen != null && !cuentaOrigen.isBlank()) {
            query.append(" and cuentaOrigen.numeroCuenta = :origen");
            params.put("origen", cuentaOrigen);
        }
        if (cuentaDestino != null && !cuentaDestino.isBlank()) {
            query.append(" and cuentaDestino.numeroCuenta = :destino");
            params.put("destino", cuentaDestino);
        }
        if (fechaInicio != null) {
            query.append(" and fecha >= :fechaInicio");
            params.put("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.append(" and fecha <= :fechaFin");
            params.put("fechaFin", fechaFin);
        }
        if (estado != null) {
            query.append(" and estado = :estado");
            params.put("estado", estado);
        }

        query.append(" order by fecha desc");
        return find(query.toString(), params);
    }
}
