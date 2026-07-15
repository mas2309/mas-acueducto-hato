package com.mas.co.service;

import com.mas.co.entity.Valores;

/**
 * Interfaz del servicio de Valor para obtener tarifas configurables.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
public interface ValorService {

    /**
     * Obtiene la configuración de valores activa.
     * 
     * @return configuración de valores
     */
    Valores obtenerValores();

    /**
     * Calcula el valor del consumo basado en rangos.
     * 
     * @param consumo consumo en m³
     * @return valor calculado
     */
    Double calcularValorConsumo(Integer consumo);
}