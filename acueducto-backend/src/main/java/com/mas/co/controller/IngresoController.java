package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.IngresoDto;
import com.mas.co.dto.PaginationInfo;
import com.mas.co.entity.enums.CategoriaIngreso;
import com.mas.co.service.IngresoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/ingresos")
@RequiredArgsConstructor
@Tag(name = "Ingresos", description = "Gestión de ingresos")
public class IngresoController {

    private final IngresoService ingresoService;

    @PostMapping
    @Operation(summary = "Crear ingreso")
    public ResponseEntity<ApiResponse<IngresoDto>> crear(@Valid @RequestBody IngresoDto dto) {
        IngresoDto creado = ingresoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(creado, "Ingreso creado exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ingreso por ID")
    public ResponseEntity<ApiResponse<IngresoDto>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ingresoService.obtener(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ingreso")
    public ResponseEntity<ApiResponse<IngresoDto>> actualizar(@PathVariable Long id, @Valid @RequestBody IngresoDto dto) {
        IngresoDto actualizado = ingresoService.actualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.success(actualizado, "Ingreso actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ingreso")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        ingresoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ingreso eliminado exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar ingresos paginados")
    public ResponseEntity<ApiResponse<List<IngresoDto>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<IngresoDto> resultado = ingresoService.listar(PageRequest.of(page, size));
        PaginationInfo pagination = PaginationInfo.builder()
                .page(resultado.getNumber()).size(resultado.getSize())
                .totalElements(resultado.getTotalElements()).totalPages(resultado.getTotalPages())
                .first(resultado.isFirst()).last(resultado.isLast())
                .numberOfElements(resultado.getNumberOfElements()).empty(resultado.isEmpty())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resultado.getContent(), pagination));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar ingresos por categoría")
    public ResponseEntity<ApiResponse<List<IngresoDto>>> listarPorCategoria(
            @PathVariable CategoriaIngreso categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<IngresoDto> resultado = ingresoService.listarPorCategoria(categoria, PageRequest.of(page, size));
        PaginationInfo pagination = PaginationInfo.builder()
                .page(resultado.getNumber()).size(resultado.getSize())
                .totalElements(resultado.getTotalElements()).totalPages(resultado.getTotalPages())
                .first(resultado.isFirst()).last(resultado.isLast())
                .numberOfElements(resultado.getNumberOfElements()).empty(resultado.isEmpty())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resultado.getContent(), pagination));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar ingresos por período")
    public ResponseEntity<ApiResponse<List<IngresoDto>>> listarPorPeriodo(
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        List<IngresoDto> ingresos = ingresoService.listarPorPeriodo(desde, hasta);
        return ResponseEntity.ok(ApiResponse.success(ingresos));
    }

    @GetMapping("/total")
    @Operation(summary = "Total de ingresos por período")
    public ResponseEntity<ApiResponse<Double>> totalPorPeriodo(
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(ingresoService.totalPorPeriodo(desde, hasta)));
    }
}
