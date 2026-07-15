package com.mas.co.usecase;

import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Caso de Uso para obtener la lectura anterior de un usuario.
 */
@Component
@RequiredArgsConstructor
public class ObtenerLecturaAnteriorUseCase {

    private final FacturaRepository facturaRepository;

    /**
     * Ejecuta la lógica para obtener la lectura anterior.
     *
     * @param usuarioId El ID del usuario.
     * @return El valor de la lectura actual de la última factura, o 0 si no hay facturas.
     */
    public Integer execute(Long usuarioId) {
        return facturaRepository.findUltimaLecturaPorUsuario(usuarioId).stream()
            .findFirst()
            .map(Factura::getLecturaActual)
            .orElse(0);
    }
}
