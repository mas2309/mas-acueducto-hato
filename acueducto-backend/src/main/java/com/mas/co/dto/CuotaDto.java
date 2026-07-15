package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mas.co.constants.BusinessConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Cuota.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de cuota del sistema")
public class CuotaDto {

    @Schema(description = "ID único de la cuota", example = "1")
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH,
          message = "La descripción debe tener entre 4 y 20 caracteres")
    @Schema(description = "Descripción de la cuota", example = "Cuota mensual agua")
    private String descripcion;

    @Schema(description = "Valor de cada cuota (calculado automáticamente)", example = "25000.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double valorCuota;

    @NotNull(message = "El valor total es obligatorio")
    @PositiveOrZero(message = "El valor total debe ser positivo")
    @Schema(description = "Valor total a pagar", example = "300000.0")
    private Double valorTotal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de creación", example = "2024-01-15")
    private LocalDate fechaInsert;

    @NotNull(message = "El número de cuotas es obligatorio")
    @Positive(message = "El número de cuotas debe ser positivo")
    @Schema(description = "Número total de cuotas", example = "12")
    private Integer numeroCuota;

    @NotNull(message = "La cuota actual es obligatoria")
    @PositiveOrZero(message = "La cuota actual debe ser positiva")
    @Schema(description = "Cuota actual en el plan", example = "3")
    private Integer cuotaActual;

    @Schema(description = "Estado activo de la cuota", example = "true")
    @Builder.Default
    private Boolean activo = BusinessConstants.QUOTA_ACTIVE;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario asociado", example = "1")
    private Long usuarioId;

    @Schema(description = "Nombre completo del usuario", example = "Juan Carlos García López")
    private String usuarioNombre;
}