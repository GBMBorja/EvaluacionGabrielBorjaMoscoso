package com.evaluacion.gabrielborjamoscoso.dto.request;

import com.evaluacion.gabrielborjamoscoso.entity.EstadoCuenta;
import com.evaluacion.gabrielborjamoscoso.entity.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para el registro de una nueva cuenta bancaria")
public class CuentaRequestDto {

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El número de cuenta debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número único de cuenta de 10 dígitos", example = "1020304050", required = true)
    private String numeroCuenta;

    @NotBlank(message = "El titular de la cuenta es obligatorio")
    @Schema(description = "Nombre completo del titular", example = "Carlos Perez", required = true)
    private String titular;

    @NotNull(message = "El tipo de cuenta es obligatorio (AHORRO o CORRIENTE)")
    @Schema(description = "Tipo de cuenta bancaria", example = "AHORRO", required = true)
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial debe ser mayor o igual a 0")
    @Schema(description = "Saldo inicial de la cuenta (>= 0)", example = "1500.00", required = true)
    private BigDecimal saldoInicial;

    @Schema(description = "Estado de la cuenta (ACTIVA o INACTIVA). Por defecto ACTIVA", example = "ACTIVA")
    private EstadoCuenta estado;
}
