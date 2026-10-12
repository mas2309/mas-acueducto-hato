package com.mas.co.repository;

import com.mas.co.entity.NotaInformativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad NotaInformativa.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface NotaInformativaRepository extends JpaRepository<NotaInformativa, Long> {

    /**
     * Obtiene la nota configurada (debe haber solo un registro).
     */
    Optional<NotaInformativa> findFirstByOrderByIdDesc();
}
