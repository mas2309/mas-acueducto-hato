package com.mas.co.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de una cuota cobrada en una factura.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cuota cobrada en una factura")
public class CuotaFacturaDto {

    @Schema(description = "ID de la cuota", example = "1")
    private Long cuotaId;

    @Schema(description = "Descripción de la cuota", example = "Obras tanque")
    private String descripcion;

    @Schema(description = "Número de la cuota cobrada", example = "1")
    private Integer numeroCuota;

    @Schema(description = "Número total de cuotas", example = "2")
    private Integer totalCuotas;

    @Schema(description = "Valor cobrado", example = "25000.0")
    private Double valor;
}
