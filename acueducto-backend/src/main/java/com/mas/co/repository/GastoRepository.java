package com.mas.co.repository;

import com.mas.co.entity.Gasto;
import com.mas.co.entity.enums.CategoriaGasto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    Page<Gasto> findByOrderByFechaDesc(Pageable pageable);

    Page<Gasto> findByCategoriaOrderByFechaDesc(CategoriaGasto categoria, Pageable pageable);

    Page<Gasto> findByPagadoOrderByFechaDesc(Boolean pagado, Pageable pageable);

    List<Gasto> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.fecha BETWEEN :desde AND :hasta")
    Double sumMontoByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.categoria = :categoria AND g.fecha BETWEEN :desde AND :hasta")
    Double sumMontoByCategoriaAndFechaBetween(CategoriaGasto categoria, LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.pagado = false")
    Double sumGastosPendientes();
}
