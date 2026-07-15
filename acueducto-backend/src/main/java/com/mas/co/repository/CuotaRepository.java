package com.mas.co.repository;

import com.mas.co.entity.Cuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cuota.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    /**
     * Busca cuota por ID y que esté activa.
     */
    Optional<Cuota> findByIdAndActivoTrue(Long id);

    /**
     * Obtiene la primera cuota activa de un usuario, ordenada por fecha de inserción descendente.
     * Ideal para encontrar la cuota más reciente que aún está activa.
     *
     * @param usuarioId ID del usuario.
     * @return Un {@link Optional} que contiene la cuota si se encuentra, o vacío si no.
     */
    Optional<Cuota> findFirstByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(Long usuarioId);

    /**
     * Obtiene todas las cuotas activas paginadas.
     */
    Page<Cuota> findByActivoTrue(Pageable pageable);

    /**
     * Obtiene cuotas por usuario ID y activas.
     */
    Page<Cuota> findByUsuarioIdAndActivoTrue(Long usuarioId, Pageable pageable);

    /**
     * Busca cuotas por descripción.
     */
    @Query("SELECT c FROM Cuota c WHERE c.activo = true AND " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Cuota> findByDescripcionContainingIgnoreCase(@Param("termino") String termino, Pageable pageable);

    /**
     * Obtiene todas las cuotas activas ordenadas por fecha.
     */
    List<Cuota> findByActivoTrueOrderByFechaInsertDesc();

    /**
     * Obtiene cuotas activas por usuario ordenadas por fecha.
     */
    List<Cuota> findByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(Long usuarioId);
}