package com.evaluacion.gabrielborjamoscoso.service;

import java.math.BigDecimal;

public interface LimiteDiarioService {

    void validarLimiteDiario(Long cuentaOrigenId, BigDecimal monto);

    BigDecimal obtenerAcumuladoHoy(Long cuentaOrigenId);

    BigDecimal obtenerDisponibleHoy(Long cuentaOrigenId);
}
