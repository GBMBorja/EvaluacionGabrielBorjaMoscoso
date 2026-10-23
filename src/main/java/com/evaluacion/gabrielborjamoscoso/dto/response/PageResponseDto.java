package com.evaluacion.gabrielborjamoscoso.dto.response;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta paginada genérica")
public class PageResponseDto<T> {

    @Schema(description = "Elementos de la página actual")
    private List<T> contenido;

    @Schema(description = "Número de página actual (base 0)", example = "0")
    private int pagina;

    @Schema(description = "Tamaño de la página", example = "10")
    private int tamano;

    @Schema(description = "Total de elementos disponibles", example = "45")
    private long totalElementos;

    @Schema(description = "Total de páginas disponibles", example = "5")
    private int totalPaginas;
}
