package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mas.co.constants.BusinessConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para ingreso de lectura de consumo.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para ingreso de lectura de consumo")
public class LecturaDto {

    @Schema(description = "ID de la factura/lectura", example = "1")
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario", example = "1", required = true)
    private Long usuarioId;

    @Schema(description = "Nombre completo del usuario", example = "Juan Carlos García López")
    private String usuarioNombre;

    @NotNull(message = "El mes es obligatorio")
    @Schema(description = "Mes de la lectura", example = "Enero", required = true)
    private String mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "El año debe ser mayor a 2020")
    @Max(value = 2050, message = "El año debe ser menor a 2050")
    @Schema(description = "Año de la lectura", example = "2024", required = true)
    private Integer anio;

    @NotNull(message = "La lectura actual es obligatoria")
    @Min(value = BusinessConstants.MIN_READING_VALUE, message = "La lectura debe ser mayor o igual a 0")
    @Max(value = BusinessConstants.MAX_READING_VALUE, message = "La lectura debe ser menor a 999999")
    @Schema(description = "Lectura actual del medidor", example = "1250", required = true)
    private Integer lecturaActual;

    @Schema(description = "Lectura anterior del medidor", example = "1200")
    @PositiveOrZero(message = "La lectura anterior debe ser positiva")
    private Integer lecturaAnterior;

    @Schema(description = "Consumo calculado (m³)", example = "50")
    private Integer consumo;

    @Schema(description = "Valor del consumo", example = "75000.0")
    private Double valorConsumo;

    @Schema(description = "Otros cobros adicionales", example = "10000.0")
    @PositiveOrZero(message = "Los otros cobros deben ser positivos")
    private Double otrosCobros;

    @Schema(description = "Descripción de otros cobros", example = "Reconexión")
    private String otrosCobrosDescripcion;

    @Schema(description = "Deuda anterior pendiente", example = "25000.0")
    @PositiveOrZero(message = "La deuda anterior debe ser positiva")
    private Double deudaAnterior;

    @Schema(description = "ID de cuota asociada", example = "1")
    private Long cuotaId;

    @Schema(description = "Valor de cuota", example = "25000.0")
    private Double valorCuota;

    @Schema(description = "Cargo fijo del servicio", example = "15000.0")
    @PositiveOrZero(message = "El cargo fijo debe ser positivo")
    private Double cargoFijo;

    @Schema(description = "Valor total de la factura", example = "135000.0")
    private Double valorTotal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de ingreso", example = "2024-01-15")
    private LocalDate fechaIngreso;

    @Schema(description = "Estado de pago", example = "NO_PAYMENT")
    private String estadoPago;

    @Schema(description = "Pago en efectivo", example = "false")
    private Boolean pago;

    @Schema(description = "Pago por banco", example = "false")
    private Boolean pagoBanco;

    @Schema(description = "Valor por no pago", example = "2000.0")
    private Double noPago;
}