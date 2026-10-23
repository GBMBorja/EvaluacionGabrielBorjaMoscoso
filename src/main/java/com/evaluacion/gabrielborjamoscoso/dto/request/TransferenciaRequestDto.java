package com.evaluacion.gabrielborjamoscoso.dto.request;

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
@Schema(description = "Solicitud de ejecución de transferencia entre cuentas bancarias")
public class TransferenciaRequestDto {

    @NotBlank(message = "El número de cuenta de origen es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El número de cuenta de origen debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número de cuenta de origen (10 dígitos)", example = "1020304050", required = true)
    private String numeroCuentaOrigen;

    @NotBlank(message = "El número de cuenta de destino es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El número de cuenta de destino debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número de cuenta de destino (10 dígitos)", example = "9080706050", required = true)
    private String numeroCuentaDestino;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto a transferir debe ser mayor a cero")
    @Schema(description = "Monto monetario a transferir (mayor a 0)", example = "250.00", required = true)
    private BigDecimal monto;
}
