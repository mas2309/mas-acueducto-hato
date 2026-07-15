package com.mas.co.repository;

import com.mas.co.entity.Ingreso;
import com.mas.co.entity.enums.CategoriaIngreso;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    Page<Ingreso> findByOrderByFechaDesc(Pageable pageable);

    Page<Ingreso> findByCategoriaOrderByFechaDesc(CategoriaIngreso categoria, Pageable pageable);

    List<Ingreso> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.fecha BETWEEN :desde AND :hasta")
    Double sumMontoByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.categoria = :categoria AND i.fecha BETWEEN :desde AND :hasta")
    Double sumMontoByCategoriaAndFechaBetween(CategoriaIngreso categoria, LocalDate desde, LocalDate hasta);
}
