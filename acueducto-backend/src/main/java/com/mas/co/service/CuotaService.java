package com.mas.co.service;

import com.mas.co.dto.CuotaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de Cuota.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
public interface CuotaService {

    /**
     * Crea una nueva cuota.
     * 
     * @param cuotaDto datos de la cuota
     * @return cuota creada
     */
    CuotaDto crearCuota(CuotaDto cuotaDto);

    /**
     * Obtiene cuota por ID.
     * 
     * @param id ID de la cuota
     * @return cuota encontrada
     */
    CuotaDto obtenerCuota(Long id);

    /**
     * Actualiza una cuota existente.
     * 
     * @param id ID de la cuota
     * @param cuotaDto nuevos datos
     * @return cuota actualizada
     */
    CuotaDto actualizarCuota(Long id, CuotaDto cuotaDto);

    /**
     * Desactiva una cuota.
     * 
     * @param id ID de la cuota
     */
    void desactivarCuota(Long id);

    /**
     * Elimina una cuota físicamente.
     * 
     * @param id ID de la cuota
     */
    void eliminarCuota(Long id);

    /**
     * Obtiene cuotas paginadas.
     * 
     * @param pageable información de paginación
     * @return página de cuotas
     */
    Page<CuotaDto> obtenerCuotas(Pageable pageable);

    /**
     * Obtiene cuotas por usuario.
     * 
     * @param usuarioId ID del usuario
     * @param pageable información de paginación
     * @return página de cuotas del usuario
     */
    Page<CuotaDto> obtenerCuotasPorUsuario(Long usuarioId, Pageable pageable);

    /**
     * Busca cuotas por descripción.
     * 
     * @param termino término de búsqueda
     * @param pageable información de paginación
     * @return página de cuotas encontradas
     */
    Page<CuotaDto> buscarCuotas(String termino, Pageable pageable);

    /**
     * Obtiene todas las cuotas activas.
     * 
     * @return lista de cuotas activas
     */
    List<CuotaDto> obtenerCuotasActivas();

    /**
     * Obtiene cuotas activas por usuario.
     * 
     * @param usuarioId ID del usuario
     * @return lista de cuotas activas del usuario
     */
    List<CuotaDto> obtenerCuotasActivasPorUsuario(Long usuarioId);

    /**
     * Procesa el pago de una cuota (incrementa cuota actual).
     * 
     * @param id ID de la cuota
     * @return cuota actualizada
     */
    CuotaDto procesarPagoCuota(Long id);
}