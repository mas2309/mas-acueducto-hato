package com.mas.co.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Información de paginación para respuestas de la API.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de paginación")
public class PaginationInfo {

    @Schema(description = "Número de página actual (base 0)", example = "0")
    private Integer page;

    @Schema(description = "Tamaño de página", example = "20")
    private Integer size;

    @Schema(description = "Total de elementos", example = "150")
    private Long totalElements;

    @Schema(description = "Total de páginas", example = "8")
    private Integer totalPages;

    @Schema(description = "Indica si es la primera página", example = "true")
    private Boolean first;

    @Schema(description = "Indica si es la última página", example = "false")
    private Boolean last;

    @Schema(description = "Número de elementos en la página actual", example = "20")
    private Integer numberOfElements;

    @Schema(description = "Indica si la página está vacía", example = "false")
    private Boolean empty;
}