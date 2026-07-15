package com.mas.co.dto;

import com.mas.co.entity.enums.CategoriaGasto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoDto {

    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaGasto categoria;

    private Boolean pagado;
    private LocalDate fechaPago;
    private String soporteUrl;
    private String soporteNombre;
    private String responsable;
    private String registradoPorNombre;
    private Long registradoPorId;
    private LocalDate fechaRegistro;
}
