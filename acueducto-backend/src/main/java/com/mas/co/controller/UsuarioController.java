package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.PaginationInfo;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * Controlador REST para gestión de usuarios.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@RestController
@RequestMapping(ApiConstants.USERS_PATH)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema de acueducto")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @PostMapping
  @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
  public ResponseEntity<ApiResponse<UsuarioDto>> crearUsuario(
      @Valid @RequestBody UsuarioDto usuarioDto) {

    UsuarioDto usuarioCreado = usuarioService.crearUsuario(usuarioDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(usuarioCreado, "Usuario creado exitosamente"));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su ID")
  public ResponseEntity<ApiResponse<UsuarioDto>> obtenerUsuario(
      @Parameter(description = "ID del usuario") @PathVariable Long id) {

    UsuarioDto usuario = usuarioService.obtenerUsuario(id);
    return ResponseEntity.ok(ApiResponse.success(usuario));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Actualizar usuario",
      description = "Actualiza los datos de un usuario existente")
  public ResponseEntity<ApiResponse<UsuarioDto>> actualizarUsuario(
      @Parameter(description = "ID del usuario") @PathVariable Long id,
      @Valid @RequestBody UsuarioDto usuarioDto) {

    UsuarioDto usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDto);
    return ResponseEntity.ok(
        ApiResponse.success(usuarioActualizado, "Usuario actualizado exitosamente"));
  }

  @PatchMapping("/{id}/deactivate")
  @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario del sistema")
  public ResponseEntity<ApiResponse<Void>> desactivarUsuario(
      @Parameter(description = "ID del usuario") @PathVariable Long id) {

    usuarioService.desactivarUsuario(id);
    return ResponseEntity.ok(ApiResponse.success("Usuario desactivado exitosamente"));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Eliminar usuario",
      description = "Elimina físicamente un usuario del sistema")
  public ResponseEntity<ApiResponse<Void>> eliminarUsuario(
      @Parameter(description = "ID del usuario") @PathVariable Long id) {

    usuarioService.eliminarUsuario(id);
    return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente"));
  }

  @GetMapping
  @Operation(
      summary = "Listar usuarios",
      description = "Obtiene una lista paginada de usuarios activos")
  public ResponseEntity<ApiResponse<List<UsuarioDto>>> obtenerUsuarios(
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "nombre")
          String sort,
      @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC")
          String direction) {

    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

    Page<UsuarioDto> usuarios = usuarioService.obtenerUsuarios(pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(usuarios.getNumber())
            .size(usuarios.getSize())
            .totalElements(usuarios.getTotalElements())
            .totalPages(usuarios.getTotalPages())
            .first(usuarios.isFirst())
            .last(usuarios.isLast())
            .numberOfElements(usuarios.getNumberOfElements())
            .empty(usuarios.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(usuarios.getContent(), paginationInfo));
  }

  @GetMapping("/search")
  @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre o apellidos")
  public ResponseEntity<ApiResponse<List<UsuarioDto>>> buscarUsuarios(
      @Parameter(description = "Término de búsqueda") @RequestParam String q,
      @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("nombre"));
    Page<UsuarioDto> usuarios = usuarioService.buscarUsuarios(q, pageable);

    PaginationInfo paginationInfo =
        PaginationInfo.builder()
            .page(usuarios.getNumber())
            .size(usuarios.getSize())
            .totalElements(usuarios.getTotalElements())
            .totalPages(usuarios.getTotalPages())
            .first(usuarios.isFirst())
            .last(usuarios.isLast())
            .numberOfElements(usuarios.getNumberOfElements())
            .empty(usuarios.isEmpty())
            .build();

    return ResponseEntity.ok(ApiResponse.success(usuarios.getContent(), paginationInfo));
  }

  @GetMapping("/active")
  @Operation(
      summary = "Usuarios activos",
      description = "Obtiene todos los usuarios activos sin paginación")
  public ResponseEntity<ApiResponse<List<UsuarioDto>>> obtenerUsuariosActivos() {

    List<UsuarioDto> usuarios = usuarioService.obtenerUsuariosActivos();
    return ResponseEntity.ok(ApiResponse.success(usuarios));
  }
}
