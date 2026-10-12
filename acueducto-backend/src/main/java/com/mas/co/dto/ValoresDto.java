package com.mas.co.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la configuración de tarifas: cargo fijo, recargo por no pago y rangos de consumo.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Configuración de tarifas del sistema")
public class ValoresDto {

    @NotNull(message = "El cargo fijo es obligatorio")
    @PositiveOrZero(message = "El cargo fijo debe ser positivo")
    @Schema(description = "Cargo fijo mensual", example = "3000.0")
    private Double cargoFijo;

    @NotNull(message = "El recargo por no pago es obligatorio")
    @PositiveOrZero(message = "El recargo por no pago debe ser positivo")
    @Schema(description = "Recargo cuando la factura anterior no fue pagada", example = "2000.0")
    private Double noPago;

    @NotNull(message = "El límite del rango 1 es obligatorio")
    @Positive(message = "El límite del rango 1 debe ser positivo")
    @Schema(description = "Consumo máximo (m³) del rango 1", example = "10")
    private Integer rangoUno;

    @NotNull(message = "El límite del rango 2 es obligatorio")
    @Positive(message = "El límite del rango 2 debe ser positivo")
    @Schema(description = "Consumo máximo (m³) del rango 2", example = "20")
    private Integer rangoDos;

    @NotNull(message = "El límite del rango 3 es obligatorio")
    @Positive(message = "El límite del rango 3 debe ser positivo")
    @Schema(description = "Consumo máximo (m³) del rango 3. El rango 4 aplica por encima", example = "30")
    private Integer rangoTres;

    @NotNull(message = "El valor del rango 1 es obligatorio")
    @PositiveOrZero(message = "El valor del rango 1 debe ser positivo")
    @Schema(description = "Valor por m³ en el rango 1", example = "600")
    private Integer valorUno;

    @NotNull(message = "El valor del rango 2 es obligatorio")
    @PositiveOrZero(message = "El valor del rango 2 debe ser positivo")
    @Schema(description = "Valor por m³ en el rango 2", example = "1000")
    private Integer valorDos;

    @NotNull(message = "El valor del rango 3 es obligatorio")
    @PositiveOrZero(message = "El valor del rango 3 debe ser positivo")
    @Schema(description = "Valor por m³ en el rango 3", example = "1500")
    private Integer valorTres;

    @NotNull(message = "El valor del rango 4 es obligatorio")
    @PositiveOrZero(message = "El valor del rango 4 debe ser positivo")
    @Schema(description = "Valor por m³ en el rango 4", example = "3500")
    private Integer valorCuatro;
}
