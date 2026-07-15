package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.PaginationInfo;
import com.mas.co.service.CuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controladores REST para gestión de cuotas.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@RestController
@RequestMapping(ApiConstants.QUOTAS_PATH)
@RequiredArgsConstructor
@Tag(name = "Cuotas", description = "Gestión de cuotas del sistema de acueducto")
public class CuotaController {

    private final CuotaService cuotaService;

    @PostMapping
    @Operation(summary = "Crear cuota", description = "Crea una nueva cuota en el sistema")
    public ResponseEntity<ApiResponse<CuotaDto>> crearCuota(
            @Valid @RequestBody CuotaDto cuotaDto) {

        CuotaDto cuotaCreada = cuotaService.crearCuota(cuotaDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cuotaCreada, "Cuota creada exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuota", description = "Obtiene una cuota por su ID")
    public ResponseEntity<ApiResponse<CuotaDto>> obtenerCuota(
            @Parameter(description = "ID de la cuota") @PathVariable Long id) {

        CuotaDto cuota = cuotaService.obtenerCuota(id);
        return ResponseEntity.ok(ApiResponse.success(cuota));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuota", description = "Actualiza los datos de una cuota existente")
    public ResponseEntity<ApiResponse<CuotaDto>> actualizarCuota(
            @Parameter(description = "ID de la cuota") @PathVariable Long id,
            @Valid @RequestBody CuotaDto cuotaDto) {

        CuotaDto cuotaActualizada = cuotaService.actualizarCuota(id, cuotaDto);
        return ResponseEntity.ok(ApiResponse.success(cuotaActualizada, "Cuota actualizada exitosamente"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar cuota", description = "Desactiva una cuota del sistema")
    public ResponseEntity<ApiResponse<Void>> desactivarCuota(
            @Parameter(description = "ID de la cuota") @PathVariable Long id) {

        cuotaService.desactivarCuota(id);
        return ResponseEntity.ok(ApiResponse.success("Cuota desactivada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuota", description = "Elimina físicamente una cuota del sistema")
    public ResponseEntity<ApiResponse<Void>> eliminarCuota(
            @Parameter(description = "ID de la cuota") @PathVariable Long id) {

        cuotaService.eliminarCuota(id);
        return ResponseEntity.ok(ApiResponse.success("Cuota eliminada exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar cuotas", description = "Obtiene una lista paginada de cuotas activas")
    public ResponseEntity<ApiResponse<List<CuotaDto>>> obtenerCuotas(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaInsert") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<CuotaDto> cuotas = cuotaService.obtenerCuotas(pageable);

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .page(cuotas.getNumber())
                .size(cuotas.getSize())
                .totalElements(cuotas.getTotalElements())
                .totalPages(cuotas.getTotalPages())
                .first(cuotas.isFirst())
                .last(cuotas.isLast())
                .numberOfElements(cuotas.getNumberOfElements())
                .empty(cuotas.isEmpty())
                .build();

        return ResponseEntity.ok(ApiResponse.success(cuotas.getContent(), paginationInfo));
    }

    @GetMapping("/user/{usuarioId}")
    @Operation(summary = "Cuotas por usuario", description = "Obtiene cuotas paginadas de un usuario específico")
    public ResponseEntity<ApiResponse<List<CuotaDto>>> obtenerCuotasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaInsert"));
        Page<CuotaDto> cuotas = cuotaService.obtenerCuotasPorUsuario(usuarioId, pageable);

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .page(cuotas.getNumber())
                .size(cuotas.getSize())
                .totalElements(cuotas.getTotalElements())
                .totalPages(cuotas.getTotalPages())
                .first(cuotas.isFirst())
                .last(cuotas.isLast())
                .numberOfElements(cuotas.getNumberOfElements())
                .empty(cuotas.isEmpty())
                .build();

        return ResponseEntity.ok(ApiResponse.success(cuotas.getContent(), paginationInfo));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar cuotas", description = "Busca cuotas por descripción")
    public ResponseEntity<ApiResponse<List<CuotaDto>>> buscarCuotas(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("descripcion"));
        Page<CuotaDto> cuotas = cuotaService.buscarCuotas(q, pageable);

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .page(cuotas.getNumber())
                .size(cuotas.getSize())
                .totalElements(cuotas.getTotalElements())
                .totalPages(cuotas.getTotalPages())
                .first(cuotas.isFirst())
                .last(cuotas.isLast())
                .numberOfElements(cuotas.getNumberOfElements())
                .empty(cuotas.isEmpty())
                .build();

        return ResponseEntity.ok(ApiResponse.success(cuotas.getContent(), paginationInfo));
    }

    @GetMapping("/active")
    @Operation(summary = "Cuotas activas", description = "Obtiene todas las cuotas activas sin paginación")
    public ResponseEntity<ApiResponse<List<CuotaDto>>> obtenerCuotasActivas() {

        List<CuotaDto> cuotas = cuotaService.obtenerCuotasActivas();
        return ResponseEntity.ok(ApiResponse.success(cuotas));
    }

    @GetMapping("/active/user/{usuarioId}")
    @Operation(summary = "Cuotas activas por usuario", description = "Obtiene cuotas activas de un usuario específico")
    public ResponseEntity<ApiResponse<List<CuotaDto>>> obtenerCuotasActivasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {

        List<CuotaDto> cuotas = cuotaService.obtenerCuotasActivasPorUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(cuotas));
    }

}