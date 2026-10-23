package com.evaluacion.gabrielborjamoscoso.dto.response;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resumen de actividad operativa de la cuenta")
public class CuentaResumenDto {

    @Schema(description = "Identificador de la cuenta", example = "1")
    private Long cuentaId;

    @Schema(description = "Número de cuenta", example = "1020304050")
    private String numeroCuenta;

    @Schema(description = "Nombre del titular", example = "Carlos Perez")
    private String titular;

    @Schema(description = "Saldo actual disponible", example = "4350.00")
    private BigDecimal saldoActual;

    @Schema(description = "Monto total transferido en el día calendario actual", example = "1200.00")
    private BigDecimal totalTransferidoHoy;

    @Schema(description = "Cantidad total de transferencias realizadas en el día de hoy", example = "3")
    private long cantidadTransferenciasHoy;

    @Schema(description = "Cantidad de transferencias marcadas como sospechosas", example = "1")
    private long cantidadSospechosas;
}
