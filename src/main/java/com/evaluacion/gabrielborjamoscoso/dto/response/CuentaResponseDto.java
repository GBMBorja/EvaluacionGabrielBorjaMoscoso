package com.evaluacion.gabrielborjamoscoso.dto.response;

import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.entity.TipoCuenta;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información detallada de una cuenta bancaria")
public class CuentaResponseDto {

    @Schema(description = "Identificador único de la cuenta", example = "1")
    private Long id;

    @Schema(description = "Número único de cuenta de 10 dígitos", example = "1020304050")
    private String numeroCuenta;

    @Schema(description = "Titular de la cuenta", example = "Carlos Perez")
    private String titular;

    @Schema(description = "Tipo de cuenta (AHORRO o CORRIENTE)", example = "AHORRO")
    private TipoCuenta tipoCuenta;

    @Schema(description = "Saldo disponible actual", example = "1500.00")
    private BigDecimal saldo;

    @Schema(description = "Estado de la cuenta (ACTIVA o INACTIVA)", example = "ACTIVA")
    private EstadoCuenta estado;

    @Schema(description = "Fecha de registro de la cuenta", example = "2026-10-21T10:30:00")
    private LocalDateTime fechaCreacion;
}
