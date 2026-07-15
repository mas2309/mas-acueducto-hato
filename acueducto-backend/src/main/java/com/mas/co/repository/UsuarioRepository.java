package com.mas.co.repository;

import com.mas.co.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuarios activos.
     * 
     * @param pageable información de paginación
     * @return página de usuarios activos
     */
    Page<Usuario> findByActivoTrue(Pageable pageable);

    /**
     * Busca usuario activo por ID.
     * 
     * @param id ID del usuario
     * @return usuario activo si existe
     */
    Optional<Usuario> findByIdAndActivoTrue(Long id);

    /**
     * Busca usuarios por nombre o apellidos.
     * 
     * @param nombre nombre a buscar
     * @param apellidos apellidos a buscar
     * @param pageable información de paginación
     * @return página de usuarios encontrados
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%')))")
    Page<Usuario> findByNombreOrApellidosContainingIgnoreCase(
            @Param("nombre") String nombre, 
            @Param("apellidos") String apellidos, 
            Pageable pageable);

    /**
     * Obtiene todos los usuarios activos.
     * 
     * @return lista de usuarios activos
     */
    List<Usuario> findByActivoTrueOrderByNombreAsc();
}