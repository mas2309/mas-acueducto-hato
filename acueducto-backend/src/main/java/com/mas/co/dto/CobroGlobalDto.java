package com.mas.co.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mas.co.constants.BusinessConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para transferencia de datos de CobroGlobal, con el resumen de avance del cobro.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cobro global aplicado a todos los usuarios")
public class CobroGlobalDto {

    @Schema(description = "ID único del cobro global", example = "1")
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH,
          message = "La descripción debe tener entre 4 y 20 caracteres")
    @Schema(description = "Descripción del cobro, se imprime en la factura", example = "Obras tanque")
    private String descripcion;

    @NotNull(message = "El valor total es obligatorio")
    @Positive(message = "El valor total debe ser positivo")
    @Schema(description = "Valor total que aporta cada usuario", example = "100000.0")
    private Double valorTotal;

    @NotNull(message = "El número de cuotas es obligatorio")
    @Positive(message = "El número de cuotas debe ser positivo")
    @Schema(description = "Número de cuotas en que se cobra", example = "2")
    private Integer numeroCuotas;

    @Schema(description = "Valor de cada cuota (calculado automáticamente)", example = "50000.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double valorCuota;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de creación", example = "2024-01-15")
    private LocalDate fechaCreacion;

    @Schema(description = "Estado activo del cobro", example = "true")
    private Boolean activo;

    @Schema(description = "Usuarios a los que se aplicó el cobro", example = "120")
    private int totalUsuarios;

    @Schema(description = "Usuarios con cuotas pendientes por facturar", example = "100")
    private int usuariosEnCobro;

    @Schema(description = "Usuarios a los que ya se les facturaron todas las cuotas", example = "10")
    private int usuariosCompletados;

    @Schema(description = "Usuarios que pagaron por anticipado", example = "8")
    private int usuariosPagoAnticipado;

    @Schema(description = "Usuarios con la cuota desactivada sin terminar", example = "2")
    private int usuariosDesactivados;

    @Schema(description = "Valor total del cobro entre todos los usuarios", example = "12000000.0")
    private double valorEsperado;

    @Schema(description = "Valor ya incluido en facturas", example = "6000000.0")
    private double valorFacturado;

    @Schema(description = "Valor recibido por pagos anticipados", example = "800000.0")
    private double valorAnticipado;

    @Schema(description = "Valor pendiente por facturar", example = "5000000.0")
    private double valorPendiente;
}
