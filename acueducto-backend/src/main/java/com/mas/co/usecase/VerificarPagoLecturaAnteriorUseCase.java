package com.mas.co.usecase;

import com.mas.co.entity.Factura;
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
        return facturaRepository.findUltimaLecturaPorUsuario(usuarioId).stream()
            .findFirst()
            .map(Factura::isPagada)
            .orElse(true); // Si no hay factura anterior, se considera pagada para no aplicar cargos indebidos.
    }
}
