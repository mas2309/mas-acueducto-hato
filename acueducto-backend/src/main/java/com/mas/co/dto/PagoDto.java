package com.mas.co.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar el pago de una factura.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
}
