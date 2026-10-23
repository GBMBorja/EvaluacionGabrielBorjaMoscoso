package com.evaluacion.gabrielborjamoscoso.dto.response;

import com.evaluacion.gabrielborjamoscoso.entity.EstadoTransferencia;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalle y resultado de una operación de transferencia")
public class TransferenciaResponseDto {

    @Schema(description = "Identificador único de la transferencia", example = "1")
    private Long id;

    @Schema(description = "Número de cuenta de origen", example = "1020304050")
    private String numeroCuentaOrigen;

    @Schema(description = "Titular de la cuenta origen", example = "Carlos Perez")
    private String titularOrigen;

    @Schema(description = "Número de cuenta de destino", example = "9080706050")
    private String numeroCuentaDestino;

    @Schema(description = "Titular de la cuenta destino", example = "Maria Lopez")
    private String titularDestino;

    @Schema(description = "Monto transferido", example = "250.00")
    private BigDecimal monto;

    @Schema(description = "Fecha y hora exacta de la transferencia", example = "2026-10-21T14:15:30")
    private LocalDateTime fecha;

    @Schema(description = "Estado de la transferencia (COMPLETADA, RECHAZADA, REQUIERE_REVISION)", example = "COMPLETADA")
    private EstadoTransferencia estado;

    @Schema(description = "Indica si la transferencia fue clasificada como sospechosa", example = "false")
    private boolean sospechosa;

    @Schema(description = "Motivo por el cual fue catalogada como sospechosa si aplica", example = "El monto individual supera $5,000")
    private String motivoSospecha;

    @Schema(description = "Motivo de rechazo en caso de no haber sido procesada", example = "Saldo insuficiente")
    private String motivoRechazo;
}
