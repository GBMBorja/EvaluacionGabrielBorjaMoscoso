package com.evaluacion.gabrielborjamoscoso.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estructura estándar para respuestas de error de la API")
public class ErrorResponseDto {

    @Schema(description = "Marca de tiempo del error", example = "2026-10-21T14:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;

    @Schema(description = "Descripción del estado HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje descriptivo del error para el cliente", example = "Saldo insuficiente en la cuenta de origen")
    private String mensaje;

    @Schema(description = "Ruta del recurso solicitado", example = "/api/transferencias")
    private String path;

    @Schema(description = "Lista detallada de violaciones de validación si aplica")
    private List<String> detalles;
}
