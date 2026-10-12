package com.mas.co.service;

import com.mas.co.dto.NotaInformativaDto;

/**
 * Interfaz del servicio de la nota informativa de los recibos.
 *
 * @author MAS Development Team
 * @version 1.0
 */
public interface NotaInformativaService {

    /**
     * Obtiene la nota configurada. Si no hay ninguna devuelve una nota vacía e inactiva.
     *
     * @return nota configurada
     */
    NotaInformativaDto obtenerNota();

    /**
     * Obtiene el texto que debe imprimirse en los recibos.
     *
     * @return texto de la nota si está activa, o cadena vacía
     */
    String obtenerTextoVigente();

    /**
     * Actualiza la nota. Aplica a los recibos que se impriman desde ese momento.
     *
     * @param notaDto nueva nota
     * @return nota actualizada
     */
    NotaInformativaDto actualizarNota(NotaInformativaDto notaDto);
}
