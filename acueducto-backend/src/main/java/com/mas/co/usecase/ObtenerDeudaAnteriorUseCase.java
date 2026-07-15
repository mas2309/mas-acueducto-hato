package com.mas.co.usecase;

import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Caso de Uso para obtener la deuda anterior de un usuario, basada en la última factura no pagada.
 */
@Component
@RequiredArgsConstructor
public class ObtenerDeudaAnteriorUseCase {

    private final FacturaRepository facturaRepository;

    /**
     * Ejecuta la lógica para obtener la deuda anterior.
     *
     * @param usuarioId El ID del usuario.
     * @return El valor total de la última factura si no está pagada; de lo contrario, 0.0.
     */
    public Double execute(Long usuarioId) {
        return facturaRepository.findUltimaLecturaPorUsuario(usuarioId).stream()
            .findFirst()
            .filter(f -> !f.isPagada()) // Considerar solo si la última factura NO está pagada
            .map(Factura::getValorTotal)
            .orElse(0.0);
    }
}
