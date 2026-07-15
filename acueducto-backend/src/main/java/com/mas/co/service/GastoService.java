package com.mas.co.service;

import com.mas.co.dto.GastoDto;
import com.mas.co.entity.enums.CategoriaGasto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface GastoService {

    GastoDto crear(GastoDto dto, String username, MultipartFile soporte);

    GastoDto obtener(Long id);

    GastoDto actualizar(Long id, GastoDto dto, MultipartFile soporte);

    void eliminar(Long id);

    GastoDto marcarPagado(Long id);

    Page<GastoDto> listar(Pageable pageable);

    Page<GastoDto> listarPorCategoria(CategoriaGasto categoria, Pageable pageable);

    Page<GastoDto> listarPendientes(Pageable pageable);

    List<GastoDto> listarPorPeriodo(LocalDate desde, LocalDate hasta);

    Double totalPorPeriodo(LocalDate desde, LocalDate hasta);

    Double totalPendientes();
}
