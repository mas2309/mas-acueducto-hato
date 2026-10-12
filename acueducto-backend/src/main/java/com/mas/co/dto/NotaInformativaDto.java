package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de la nota informativa que se imprime en los recibos.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Nota informativa de los recibos")
public class NotaInformativaDto {

    @Size(max = 150, message = "La nota no puede superar los 150 caracteres")
    @Schema(description = "Texto de la nota", example = "Suspensión del servicio el 15 de marzo por mantenimiento")
    private String texto;

    @Schema(description = "Indica si la nota se imprime en los recibos", example = "true")
    private Boolean activa;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de la última modificación", example = "2026-03-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fechaActualizacion;
}
