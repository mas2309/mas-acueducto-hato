package com.mas.co.repository;

import com.mas.co.entity.CobroGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad CobroGlobal.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface CobroGlobalRepository extends JpaRepository<CobroGlobal, Long> {
}
