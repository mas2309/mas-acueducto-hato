package com.mas.co.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mas.co.config.TestSecurityConfig;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.exception.BusinessException;
import com.mas.co.security.JwtService;
import com.mas.co.security.CustomUserDetailsService;
import com.mas.co.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests de integración para UsuarioController.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@WebMvcTest(UsuarioController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Usuario Controller Tests")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioDto = UsuarioDto.builder()
                .id(1L)
                .nombre("Juan Carlos")
                .apellidos("García López")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/users - Debe crear usuario exitosamente")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        when(usuarioService.crearUsuario(any(UsuarioDto.class))).thenReturn(usuarioDto);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente"))
                .andExpect(jsonPath("$.data.nombre").value("Juan Carlos"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Debe fallar con datos inválidos")
    void shouldFailWithInvalidData() throws Exception {
        // Given
        UsuarioDto invalidDto = UsuarioDto.builder()
                .nombre("") // Nombre vacío
                .apellidos("García López")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Debe obtener usuario por ID")
    void shouldGetUserById() throws Exception {
        // Given
        when(usuarioService.obtenerUsuario(1L)).thenReturn(usuarioDto);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombre").value("Juan Carlos"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Debe retornar 400 cuando usuario no existe")
    void shouldReturn400WhenUserNotFound() throws Exception {
        // Given
        when(usuarioService.obtenerUsuario(999L))
                .thenThrow(BusinessException.userNotFound(999L));

        // When & Then
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Debe actualizar usuario exitosamente")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        UsuarioDto updatedDto = UsuarioDto.builder()
                .nombre("Juan Carlos")
                .apellidos("García López")
                .build();
        
        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario actualizado exitosamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/deactivate - Debe desactivar usuario exitosamente")
    void shouldDeactivateUserSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario desactivado exitosamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Debe eliminar usuario exitosamente")
    void shouldDeleteUserSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario eliminado exitosamente"));
    }

    @Test
    @DisplayName("GET /api/v1/users - Debe obtener usuarios paginados")
    void shouldGetPaginatedUsers() throws Exception {
        // Given
        when(usuarioService.obtenerUsuarios(any()))
                .thenReturn(new PageImpl<>(List.of(usuarioDto), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(20));
    }

    @Test
    @DisplayName("GET /api/v1/users/search - Debe buscar usuarios")
    void shouldSearchUsers() throws Exception {
        // Given
        when(usuarioService.buscarUsuarios(eq("Juan"), any()))
                .thenReturn(new PageImpl<>(List.of(usuarioDto), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/api/v1/users/search")
                        .param("q", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/users/active - Debe obtener usuarios activos")
    void shouldGetActiveUsers() throws Exception {
        // Given
        List<UsuarioDto> usuariosActivos = List.of(usuarioDto);
        when(usuarioService.obtenerUsuariosActivos()).thenReturn(usuariosActivos);

        // When & Then
        mockMvc.perform(get("/api/v1/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].nombre").value("Juan Carlos"))
                .andExpect(jsonPath("$.data[0].activo").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/users/active - Debe retornar lista vacía cuando no hay usuarios activos")
    void shouldReturnEmptyListWhenNoActiveUsers() throws Exception {
        // Given
        when(usuarioService.obtenerUsuariosActivos()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}