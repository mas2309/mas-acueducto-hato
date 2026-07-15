package com.mas.co.service;

import com.mas.co.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de Usuario.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
public interface UsuarioService {

    /**
     * Crea un nuevo usuario.
     * 
     * @param usuarioDto datos del usuario
     * @return usuario creado
     */
    UsuarioDto crearUsuario(UsuarioDto usuarioDto);

    /**
     * Obtiene usuario por ID.
     * 
     * @param id ID del usuario
     * @return usuario encontrado
     */
    UsuarioDto obtenerUsuario(Long id);

    /**
     * Actualiza un usuario existente.
     * 
     * @param id ID del usuario
     * @param usuarioDto nuevos datos
     * @return usuario actualizado
     */
    UsuarioDto actualizarUsuario(Long id, UsuarioDto usuarioDto);

    /**
     * Desactiva un usuario.
     * 
     * @param id ID del usuario
     */
    void desactivarUsuario(Long id);

    /**
     * Elimina un usuario físicamente.
     * 
     * @param id ID del usuario
     */
    void eliminarUsuario(Long id);

    /**
     * Obtiene usuarios paginados.
     * 
     * @param pageable información de paginación
     * @return página de usuarios
     */
    Page<UsuarioDto> obtenerUsuarios(Pageable pageable);

    /**
     * Busca usuarios por nombre o apellidos.
     * 
     * @param termino término de búsqueda
     * @param pageable información de paginación
     * @return página de usuarios encontrados
     */
    Page<UsuarioDto> buscarUsuarios(String termino, Pageable pageable);

    /**
     * Obtiene todos los usuarios activos.
     * 
     * @return lista de usuarios activos
     */
    List<UsuarioDto> obtenerUsuariosActivos();
}