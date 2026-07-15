package com.mas.co.repository;

import com.mas.co.entity.Valores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Valores.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Repository
public interface ValorRepository extends JpaRepository<Valores, Long> {

    /**
     * Obtiene la configuración de valores activa (debe haber solo un registro).
     */
    Optional<Valores> findFirstByOrderByIdDesc();
}