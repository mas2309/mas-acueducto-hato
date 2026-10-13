package com.mas.co.repository;

import com.mas.co.entity.Documento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Documento.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    /**
     * Obtiene los documentos, del más reciente al más antiguo.
     */
    Page<Documento> findAllByOrderByFechaSubidaDescIdDesc(Pageable pageable);

    /**
     * Busca documentos por título, del más reciente al más antiguo.
     */
    Page<Documento> findByTituloContainingIgnoreCaseOrderByFechaSubidaDescIdDesc(String titulo, Pageable pageable);
}
