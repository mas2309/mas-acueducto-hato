package com.mas.co.service;

import com.mas.co.dto.CobroGlobalDto;
import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de CobroGlobal.
 *
 * @author MAS Development Team
 * @version 1.0
 */
public interface CobroGlobalService {

    /**
     * Crea un cobro global y genera una cuota para cada usuario activo.
     *
     * @param cobroGlobalDto datos del cobro
     * @return cobro creado con su resumen
     */
    CobroGlobalDto crearCobroGlobal(CobroGlobalDto cobroGlobalDto);

    /**
     * Obtiene un cobro global con su resumen de avance.
     *
     * @param id ID del cobro global
     * @return cobro encontrado
     */
    CobroGlobalDto obtenerCobroGlobal(Long id);

    /**
     * Obtiene cobros globales paginados con su resumen de avance.
     *
     * @param pageable información de paginación
     * @return página de cobros globales
     */
    Page<CobroGlobalDto> obtenerCobrosGlobales(Pageable pageable);

    /**
     * Obtiene las cuotas generadas por un cobro global, una por usuario.
     *
     * @param id ID del cobro global
     * @return lista de cuotas del cobro
     */
    List<CuotaDto> obtenerCuotas(Long id);

    /**
     * Obtiene los usuarios activos que aún no tienen cuota en el cobro global.
     *
     * @param id ID del cobro global
     * @return lista de usuarios sin cuota en el cobro
     */
    List<UsuarioDto> obtenerUsuariosSinCobro(Long id);

    /**
     * Aplica el cobro global a un usuario que no lo tiene.
     *
     * @param id ID del cobro global
     * @param usuarioId ID del usuario
     * @return cuota generada al usuario
     */
    CuotaDto agregarUsuario(Long id, Long usuarioId);

    /**
     * Cancela un cobro global: las cuotas que siguen activas dejan de cobrarse.
     *
     * @param id ID del cobro global
     */
    void cancelarCobroGlobal(Long id);

    /**
     * Elimina un cobro global y sus cuotas. Solo es posible si ninguna cuota se ha facturado.
     *
     * @param id ID del cobro global
     */
    void eliminarCobroGlobal(Long id);
}
