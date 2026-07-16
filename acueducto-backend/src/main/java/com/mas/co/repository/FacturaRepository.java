package com.mas.co.repository;

import com.mas.co.entity.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Factura.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    /**
     * Obtiene todas las facturas ordenadas por ID descendente.
     */
    Page<Factura> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Busca factura por usuario, mes y año.
     */
    Optional<Factura> findByUsuarioIdAndMesAndAnio(Long usuarioId, String mes, Integer anio);

    /**
     * Verifica si existe factura para usuario, mes y año.
     */
    boolean existsByUsuarioIdAndMesAndAnio(Long usuarioId, String mes, Integer anio);

    /**
     * Obtiene facturas por usuario paginadas.
     */
    Page<Factura> findByUsuarioIdOrderByAnioDescMesDesc(Long usuarioId, Pageable pageable);

    /**
     * Obtiene facturas por año paginadas.
     */
    Page<Factura> findByAnioOrderByFechaIngresoDesc(Integer anio, Pageable pageable);

    /**
     * Obtiene facturas por mes y año paginadas.
     */
    Page<Factura> findByMesAndAnioOrderByFechaIngresoDesc(String mes, Integer anio, Pageable pageable);

    /**
     * Obtiene facturas pendientes de pago.
     */
    @Query("SELECT f FROM Factura f WHERE f.pago = false AND f.pagoBanco = false ORDER BY f.fechaIngreso DESC")
    Page<Factura> findFacturasPendientes(Pageable pageable);

    /**
     * Obtiene facturas pagadas.
     */
    @Query("SELECT f FROM Factura f WHERE f.pago = true OR f.pagoBanco = true ORDER BY f.fechaPago DESC")
    Page<Factura> findFacturasPagadas(Pageable pageable);

    /**
     * Obtiene la última factura de un usuario ordenada por año y mes descendente.
     */
    @Query("SELECT f FROM Factura f WHERE f.usuario.id = :usuarioId ORDER BY f.anio DESC, " +
           "CASE f.mes " +
           "WHEN 'Enero' THEN 1 WHEN 'Febrero' THEN 2 WHEN 'Marzo' THEN 3 WHEN 'Abril' THEN 4 " +
           "WHEN 'Mayo' THEN 5 WHEN 'Junio' THEN 6 WHEN 'Julio' THEN 7 WHEN 'Agosto' THEN 8 " +
           "WHEN 'Septiembre' THEN 9 WHEN 'Octubre' THEN 10 WHEN 'Noviembre' THEN 11 WHEN 'Diciembre' THEN 12 " +
           "END DESC")
    List<Factura> findUltimaLecturaPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene únicamente la última factura de un usuario (LIMIT 1 en BD).
     */
    @Query(value = "SELECT * FROM acueducto.facturas f WHERE f.usuario_id = :usuarioId " +
           "ORDER BY f.anio DESC, " +
           "CASE f.mes " +
           "WHEN 'Enero' THEN 1 WHEN 'Febrero' THEN 2 WHEN 'Marzo' THEN 3 WHEN 'Abril' THEN 4 " +
           "WHEN 'Mayo' THEN 5 WHEN 'Junio' THEN 6 WHEN 'Julio' THEN 7 WHEN 'Agosto' THEN 8 " +
           "WHEN 'Septiembre' THEN 9 WHEN 'Octubre' THEN 10 WHEN 'Noviembre' THEN 11 WHEN 'Diciembre' THEN 12 " +
           "END DESC LIMIT 1", nativeQuery = true)
    Optional<Factura> findUltimaFacturaPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene deuda pendiente de un usuario.
     */
    @Query("SELECT COALESCE(SUM(f.valorTotal), 0.0) FROM Factura f WHERE f.usuario.id = :usuarioId " +
           "AND f.pago = false AND f.pagoBanco = false")
    Double findDeudaPendientePorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Verifica si la última factura de un usuario está pagada (por efectivo o banco).
     */
    @Query(value = "SELECT CASE WHEN (f.pago = true OR f.pago_banco = true) THEN true ELSE false END " +
           "FROM acueducto.facturas f WHERE f.usuario_id = :usuarioId " +
           "ORDER BY f.anio DESC, " +
           "CASE f.mes " +
           "WHEN 'Enero' THEN 1 WHEN 'Febrero' THEN 2 WHEN 'Marzo' THEN 3 WHEN 'Abril' THEN 4 " +
           "WHEN 'Mayo' THEN 5 WHEN 'Junio' THEN 6 WHEN 'Julio' THEN 7 WHEN 'Agosto' THEN 8 " +
           "WHEN 'Septiembre' THEN 9 WHEN 'Octubre' THEN 10 WHEN 'Noviembre' THEN 11 WHEN 'Diciembre' THEN 12 " +
           "END DESC LIMIT 1", nativeQuery = true)
    Optional<Boolean> findUltimaFacturaPagada(@Param("usuarioId") Long usuarioId);

    /**
     * Busca facturas por término en nombre de usuario.
     */
    @Query("SELECT f FROM Factura f WHERE " +
           "LOWER(f.usuario.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(f.usuario.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "ORDER BY f.fechaIngreso DESC")
    Page<Factura> findByUsuarioNombreContaining(@Param("termino") String termino, Pageable pageable);

    /**
     * Verifica existencia con consulta nativa para evitar cache.
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM acueducto.facturas f " +
           "INNER JOIN acueducto.usuarios u ON f.usuario_id = u.id " +
           "WHERE u.id = :usuarioId AND f.mes = :mes AND f.anio = :anio", 
           nativeQuery = true)
    boolean existsFacturaNative(@Param("usuarioId") Long usuarioId, 
                               @Param("mes") String mes, 
                               @Param("anio") Integer anio);

    /**
     * Consulta directa para obtener IDs de facturas existentes.
     */
    @Query(value = "SELECT f.id FROM acueducto.facturas f " +
           "WHERE f.usuario_id = :usuarioId AND f.mes = :mes AND f.anio = :anio", 
           nativeQuery = true)
    List<Long> findFacturaIds(@Param("usuarioId") Long usuarioId, 
                             @Param("mes") String mes, 
                             @Param("anio") Integer anio);

    /**
     * Debug: Obtiene todos los registros de facturas para verificar contenido.
     */
    @Query(value = "SELECT f.id, f.usuario_id, f.mes, f.anio FROM acueducto.facturas f " +
           "WHERE f.usuario_id = :usuarioId", 
           nativeQuery = true)
    List<Object[]> debugFacturasUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene facturas por año.
     */
    List<Factura> findByAnio(Integer anio);

    /**
     * Obtiene facturas por mes y año.
     */
    List<Factura> findByMesAndAnio(String mes, Integer anio);

    /**
     * Obtiene años distintos disponibles.
     */
    @Query("SELECT DISTINCT f.anio FROM Factura f ORDER BY f.anio DESC")
    List<Integer> findAniosDisponibles();
}