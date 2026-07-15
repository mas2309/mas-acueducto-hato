package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mas.co.constants.ApiConstants;
import com.mas.co.constants.BusinessConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la entidad Usuario.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del usuario del sistema de acueducto")
public class UsuarioDto {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    @NotBlank(message = ApiConstants.VALIDATION_NOT_EMPTY)
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH, 
          message = ApiConstants.VALIDATION_SIZE_MIN_MAX)
    private String nombre;

    @Schema(description = "Apellidos del usuario", example = "García López")
    @NotBlank(message = ApiConstants.VALIDATION_NOT_EMPTY)
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH, 
          message = ApiConstants.VALIDATION_SIZE_MIN_MAX)
    private String apellidos;

    @Schema(description = "Fecha de registro del usuario", example = "2024-01-15")
    private LocalDate fechaInsert;

    @Schema(description = "Estado activo del usuario", example = "true")
    private Boolean activo;

    @JsonProperty("nombreCompleto")
    @Schema(description = "Nombre completo del usuario", example = "Juan Carlos García López", accessMode = Schema.AccessMode.READ_ONLY)
    public String getNombreCompleto() {
        return (nombre != null && apellidos != null) ? 
            String.format("%s %s", nombre, apellidos) : null;
    }
}