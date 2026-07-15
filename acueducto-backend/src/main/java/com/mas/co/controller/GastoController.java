package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.GastoDto;
import com.mas.co.dto.PaginationInfo;
import com.mas.co.entity.enums.CategoriaGasto;
import com.mas.co.service.GastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/gastos")
@RequiredArgsConstructor
@Tag(name = "Gastos", description = "Gestión de gastos con soporte de archivos")
public class GastoController {

    private final GastoService gastoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear gasto", description = "Crea un gasto con soporte opcional (imagen/PDF)")
    public ResponseEntity<ApiResponse<GastoDto>> crear(
            @RequestPart("gasto") GastoDto dto,
            @RequestPart(value = "soporte", required = false) MultipartFile soporte,
            Authentication authentication) {
        GastoDto creado = gastoService.crear(dto, authentication.getName(), soporte);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(creado, "Gasto creado exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener gasto por ID")
    public ResponseEntity<ApiResponse<GastoDto>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(gastoService.obtener(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar gasto")
    public ResponseEntity<ApiResponse<GastoDto>> actualizar(
            @PathVariable Long id,
            @RequestPart("gasto") GastoDto dto,
            @RequestPart(value = "soporte", required = false) MultipartFile soporte) {
        GastoDto actualizado = gastoService.actualizar(id, dto, soporte);
        return ResponseEntity.ok(ApiResponse.success(actualizado, "Gasto actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar gasto")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        gastoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Gasto eliminado exitosamente"));
    }

    @PatchMapping("/{id}/pagar")
    @Operation(summary = "Marcar gasto como pagado")
    public ResponseEntity<ApiResponse<GastoDto>> marcarPagado(@PathVariable Long id) {
        GastoDto pagado = gastoService.marcarPagado(id);
        return ResponseEntity.ok(ApiResponse.success(pagado, "Gasto marcado como pagado"));
    }

    @GetMapping
    @Operation(summary = "Listar gastos paginados")
    public ResponseEntity<ApiResponse<List<GastoDto>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GastoDto> resultado = gastoService.listar(PageRequest.of(page, size));
        PaginationInfo pagination = PaginationInfo.builder()
                .page(resultado.getNumber()).size(resultado.getSize())
                .totalElements(resultado.getTotalElements()).totalPages(resultado.getTotalPages())
                .first(resultado.isFirst()).last(resultado.isLast())
                .numberOfElements(resultado.getNumberOfElements()).empty(resultado.isEmpty())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resultado.getContent(), pagination));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar gastos por categoría")
    public ResponseEntity<ApiResponse<List<GastoDto>>> listarPorCategoria(
            @PathVariable CategoriaGasto categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GastoDto> resultado = gastoService.listarPorCategoria(categoria, PageRequest.of(page, size));
        PaginationInfo pagination = PaginationInfo.builder()
                .page(resultado.getNumber()).size(resultado.getSize())
                .totalElements(resultado.getTotalElements()).totalPages(resultado.getTotalPages())
                .first(resultado.isFirst()).last(resultado.isLast())
                .numberOfElements(resultado.getNumberOfElements()).empty(resultado.isEmpty())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resultado.getContent(), pagination));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar gastos pendientes de pago")
    public ResponseEntity<ApiResponse<List<GastoDto>>> listarPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GastoDto> resultado = gastoService.listarPendientes(PageRequest.of(page, size));
        PaginationInfo pagination = PaginationInfo.builder()
                .page(resultado.getNumber()).size(resultado.getSize())
                .totalElements(resultado.getTotalElements()).totalPages(resultado.getTotalPages())
                .first(resultado.isFirst()).last(resultado.isLast())
                .numberOfElements(resultado.getNumberOfElements()).empty(resultado.isEmpty())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resultado.getContent(), pagination));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar gastos por período")
    public ResponseEntity<ApiResponse<List<GastoDto>>> listarPorPeriodo(
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(gastoService.listarPorPeriodo(desde, hasta)));
    }

    @GetMapping("/total")
    @Operation(summary = "Total de gastos por período")
    public ResponseEntity<ApiResponse<Double>> totalPorPeriodo(
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(gastoService.totalPorPeriodo(desde, hasta)));
    }

    @GetMapping("/total-pendientes")
    @Operation(summary = "Total de gastos pendientes de pago")
    public ResponseEntity<ApiResponse<Double>> totalPendientes() {
        return ResponseEntity.ok(ApiResponse.success(gastoService.totalPendientes()));
    }
}
