package com.evaluacion.gabrielborjamoscoso.service;

import java.math.BigDecimal;

public interface DeteccionSospechosaService {

    record ResultadoSospecha(boolean sospechosa, String motivo) {}

    ResultadoSospecha evaluarSospecha(Long cuentaOrigenId, BigDecimal monto);
}
