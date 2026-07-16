package com.mas.co.usecase;

import com.mas.co.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Caso de Uso para verificar si la última factura de un usuario fue pagada.
 */
@Component
@RequiredArgsConstructor
public class VerificarPagoLecturaAnteriorUseCase {

    private final FacturaRepository facturaRepository;

    /**
     * Ejecuta la lógica de verificación.
     *
     * @param usuarioId El ID del usuario.
     * @return {@code true} si no hay factura anterior o si la última factura fue pagada. {@code false} en caso contrario.
     */
    public boolean execute(Long usuarioId) {
        // Consulta directamente en BD si la última factura está pagada (LIMIT 1)
        return facturaRepository.findUltimaFacturaPagada(usuarioId)
            .orElse(true); // Sin factura anterior = no aplica cargo
    }
}
