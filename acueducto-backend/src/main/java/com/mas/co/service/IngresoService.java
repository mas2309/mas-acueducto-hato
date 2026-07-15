package com.mas.co.service;

import com.mas.co.dto.IngresoDto;
import com.mas.co.entity.enums.CategoriaIngreso;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IngresoService {

    IngresoDto crear(IngresoDto dto);

    IngresoDto obtener(Long id);

    IngresoDto actualizar(Long id, IngresoDto dto);

    void eliminar(Long id);

    Page<IngresoDto> listar(Pageable pageable);

    Page<IngresoDto> listarPorCategoria(CategoriaIngreso categoria, Pageable pageable);

    List<IngresoDto> listarPorPeriodo(LocalDate desde, LocalDate hasta);

    Double totalPorPeriodo(LocalDate desde, LocalDate hasta);
}
