package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PaginationInfo;
import com.mas.co.dto.PagoDto;
import com.mas.co.service.LecturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para ingreso de lecturas.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/lecturas")
@RequiredArgsConstructor
@Tag(name = "Lecturas", description = "Ingreso y gestión de lecturas de consumo")
public class LecturaController {

  private final LecturaService lecturaService;

  @PostMapping
  @Operation(
      summary = "Ingresar lectura",
      description = "Ingresa una nueva lectura y genera la factura correspondiente")
  public ResponseEntity<ApiResponse<LecturaDto>> ingresarLectura(
      @Valid @RequestBody LecturaDto lecturaDto) {

    LecturaDto lecturaIngresada = lecturaService.ingresarLectura(lecturaDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(lecturaIngresada, "Lectura ingresada exitosamente"));
  }

  @PatchMapping("/{facturaId}/pagar")
  @Operation(
      summary = "Registrar pago de factura",
      description = "Actualiza el estado de pago de una factura existente")
  public ResponseEntity<ApiResponse<LecturaDto>> registrarPago(
      @Parameter(description = "ID de la factura") @PathVariable Long facturaId,
      @Valid @RequestBody PagoDto pagoDto) {

    LecturaDto facturaPagada = lecturaService.registrarPago(facturaId, pagoDto);
    return ResponseEntity.ok(ApiResponse.success(facturaPagada, "Pago registrado exitosamente"));
  }

  @GetMapping("/ultima/{usuarioId}")
  @Operation(
      summary = "Obtener última lectura",
      description = "Obtiene la última lectura registrada de un usuario")
  public ResponseEntity<ApiResponse<LecturaDto>> obtenerUltimaLectura(
      @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {

    LecturaDto ultimaLectura = lecturaService.obtenerUltimaLectura(usuarioId);
    return ResponseEntity.ok(ApiResponse.success(ultimaLectura));
  }

  @GetMapping("/ultimas")
  @Operation(
      summary = "Últimas lecturas de todos los usuarios",
      description = "Obtiene la última factura registrada de cada usuario. Usado para precargar "
          + "el histórico en el cliente móvil y habilitar validaciones offline")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> obtenerUltimasLecturas() {

    List<LecturaDto> ultimasLecturas = lecturaService.obtenerUltimasLecturas();
    return ResponseEntity.ok(ApiResponse.success(ultimasLecturas));
  }

  @GetMapping("/usuario/{usuarioId}")
  @Operation(
      summary = "Facturas por usuario",
      description = "Obtiene facturas paginadas de un usuario específico")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> obtenerFacturasPorUsuario(
      @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LecturaDto> facturas = lecturaService.obtenerFacturasPorUsuario(usuarioId, pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(facturas.getNumber())
            .size(facturas.getSize())
            .totalElements(facturas.getTotalElements())
            .totalPages(facturas.getTotalPages())
            .first(facturas.isFirst())
            .last(facturas.isLast())
            .numberOfElements(facturas.getNumberOfElements())
            .empty(facturas.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(facturas.getContent(), paginationInfo));
  }

  @GetMapping("/periodo")
  @Operation(
      summary = "Facturas por período",
      description = "Obtiene facturas de un mes y año específico")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> obtenerFacturasPorPeriodo(
      @Parameter(description = "Mes") @RequestParam String mes,
      @Parameter(description = "Año") @RequestParam Integer anio,
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LecturaDto> facturas = lecturaService.obtenerFacturasPorPeriodo(mes, anio, pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(facturas.getNumber())
            .size(facturas.getSize())
            .totalElements(facturas.getTotalElements())
            .totalPages(facturas.getTotalPages())
            .first(facturas.isFirst())
            .last(facturas.isLast())
            .numberOfElements(facturas.getNumberOfElements())
            .empty(facturas.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(facturas.getContent(), paginationInfo));
  }

  @GetMapping("/pendientes")
  @Operation(summary = "Facturas pendientes", description = "Obtiene facturas pendientes de pago")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> obtenerFacturasPendientes(
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LecturaDto> facturas = lecturaService.obtenerFacturasPendientes(pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(facturas.getNumber())
            .size(facturas.getSize())
            .totalElements(facturas.getTotalElements())
            .totalPages(facturas.getTotalPages())
            .first(facturas.isFirst())
            .last(facturas.isLast())
            .numberOfElements(facturas.getNumberOfElements())
            .empty(facturas.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(facturas.getContent(), paginationInfo));
  }

  @GetMapping("/pagadas")
  @Operation(summary = "Facturas pagadas", description = "Obtiene facturas que han sido pagadas")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> obtenerFacturasPagadas(
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LecturaDto> facturas = lecturaService.obtenerFacturasPagadas(pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(facturas.getNumber())
            .size(facturas.getSize())
            .totalElements(facturas.getTotalElements())
            .totalPages(facturas.getTotalPages())
            .first(facturas.isFirst())
            .last(facturas.isLast())
            .numberOfElements(facturas.getNumberOfElements())
            .empty(facturas.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(facturas.getContent(), paginationInfo));
  }

  @GetMapping("/search")
  @Operation(summary = "Buscar facturas", description = "Busca facturas por nombre de usuario")
  public ResponseEntity<ApiResponse<List<LecturaDto>>> buscarFacturas(
      @Parameter(description = "Término de búsqueda") @RequestParam String q,
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LecturaDto> facturas = lecturaService.buscarFacturas(q, pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(facturas.getNumber())
            .size(facturas.getSize())
            .totalElements(facturas.getTotalElements())
            .totalPages(facturas.getTotalPages())
            .first(facturas.isFirst())
            .last(facturas.isLast())
            .numberOfElements(facturas.getNumberOfElements())
            .empty(facturas.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(facturas.getContent(), paginationInfo));
  }

  @GetMapping("/deuda/{usuarioId}")
  @Operation(
      summary = "Obtener deuda pendiente",
      description = "Obtiene el monto de deuda pendiente de un usuario")
  public ResponseEntity<ApiResponse<Double>> obtenerDeudaPendiente(
      @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {

    Double deudaPendiente = lecturaService.obtenerDeudaPendiente(usuarioId);
    return ResponseEntity.ok(ApiResponse.success(deudaPendiente));
  }

  @DeleteMapping("/{facturaId}")
  @Operation(summary = "Eliminar factura", description = "Elimina una factura por su ID")
  public ResponseEntity<ApiResponse<Void>> eliminarFactura(
      @Parameter(description = "ID de la factura") @PathVariable Long facturaId) {

    lecturaService.eliminarFactura(facturaId);
    return ResponseEntity.ok(ApiResponse.success(null, "Factura eliminada exitosamente"));
  }

  @PutMapping("/{facturaId}")
  @Operation(summary = "Actualizar factura", description = "Actualiza una factura existente")
  public ResponseEntity<ApiResponse<LecturaDto>> actualizarFactura(
      @Parameter(description = "ID de la factura") @PathVariable Long facturaId,
      @Valid @RequestBody LecturaDto lecturaDto) {

    LecturaDto facturaActualizada = lecturaService.actualizarFactura(facturaId, lecturaDto);
    return ResponseEntity.ok(
        ApiResponse.success(facturaActualizada, "Factura actualizada exitosamente"));
  }
}
