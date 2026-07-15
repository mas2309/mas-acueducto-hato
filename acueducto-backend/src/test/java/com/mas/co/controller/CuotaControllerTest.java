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
import com.mas.co.dto.CuotaDto;
import com.mas.co.exception.BusinessException;
import com.mas.co.security.JwtService;
import com.mas.co.security.CustomUserDetailsService;
import com.mas.co.service.CuotaService;
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
 * Tests de integración para CuotaController.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@WebMvcTest(CuotaController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Cuota Controller Tests")
class CuotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuotaService cuotaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CuotaDto cuotaDto;

    @BeforeEach
    void setUp() {
        cuotaDto = CuotaDto.builder()
                .id(1L)
                .descripcion("Cuota mensual agua")
                .valorCuota(25000.0)
                .valorTotal(300000.0)
                .numeroCuota(12)
                .cuotaActual(0)
                .usuarioId(1L)
                .usuarioNombre("Juan Carlos García López")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/quotas - Debe crear cuota exitosamente")
    void shouldCreateQuotaSuccessfully() throws Exception {
        // Given
        when(cuotaService.crearCuota(any(CuotaDto.class))).thenReturn(cuotaDto);

        // When & Then
        mockMvc.perform(post("/api/v1/quotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuotaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cuota creada exitosamente"))
                .andExpect(jsonPath("$.data.descripcion").value("Cuota mensual agua"));
    }

    @Test
    @DisplayName("POST /api/v1/quotas - Debe fallar con datos inválidos")
    void shouldFailWithInvalidData() throws Exception {
        // Given
        CuotaDto invalidDto = CuotaDto.builder()
                .descripcion("") // Descripción vacía
                .valorTotal(300000.0)
                .numeroCuota(12)
                .usuarioId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/quotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("GET /api/v1/quotas/{id} - Debe obtener cuota por ID")
    void shouldGetQuotaById() throws Exception {
        // Given
        when(cuotaService.obtenerCuota(1L)).thenReturn(cuotaDto);

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.descripcion").value("Cuota mensual agua"));
    }

    @Test
    @DisplayName("GET /api/v1/quotas/{id} - Debe retornar 400 cuando cuota no existe")
    void shouldReturn400WhenQuotaNotFound() throws Exception {
        // Given
        when(cuotaService.obtenerCuota(999L))
                .thenThrow(BusinessException.quotaNotFound(999L));

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    @DisplayName("PUT /api/v1/quotas/{id} - Debe actualizar cuota exitosamente")
    void shouldUpdateQuotaSuccessfully() throws Exception {
        // Given
        CuotaDto updatedDto = CuotaDto.builder()
                .descripcion("Cuota actualizada")
                .valorTotal(360000.0)
                .numeroCuota(12)
                .cuotaActual(2)
                .usuarioId(1L)
                .build();
        
        when(cuotaService.actualizarCuota(eq(1L), any(CuotaDto.class)))
                .thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/v1/quotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cuota actualizada exitosamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/quotas/{id}/deactivate - Debe desactivar cuota exitosamente")
    void shouldDeactivateQuotaSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/quotas/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cuota desactivada exitosamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/quotas/{id} - Debe eliminar cuota exitosamente")
    void shouldDeleteQuotaSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/quotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cuota eliminada exitosamente"));
    }

    @Test
    @DisplayName("GET /api/v1/quotas - Debe obtener cuotas paginadas")
    void shouldGetPaginatedQuotas() throws Exception {
        // Given
        when(cuotaService.obtenerCuotas(any()))
                .thenReturn(new PageImpl<>(List.of(cuotaDto), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/api/v1/quotas")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(20));
    }

    @Test
    @DisplayName("GET /api/v1/quotas/user/{usuarioId} - Debe obtener cuotas por usuario")
    void shouldGetQuotasByUser() throws Exception {
        // Given
        when(cuotaService.obtenerCuotasPorUsuario(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(cuotaDto), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/user/1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/quotas/search - Debe buscar cuotas")
    void shouldSearchQuotas() throws Exception {
        // Given
        when(cuotaService.buscarCuotas(eq("agua"), any()))
                .thenReturn(new PageImpl<>(List.of(cuotaDto), PageRequest.of(0, 20), 1));

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/search")
                        .param("q", "agua"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/quotas/active - Debe obtener cuotas activas")
    void shouldGetActiveQuotas() throws Exception {
        // Given
        List<CuotaDto> cuotasActivas = List.of(cuotaDto);
        when(cuotaService.obtenerCuotasActivas()).thenReturn(cuotasActivas);

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].descripcion").value("Cuota mensual agua"));
    }

    @Test
    @DisplayName("GET /api/v1/quotas/active/user/{usuarioId} - Debe obtener cuotas activas por usuario")
    void shouldGetActiveQuotasByUser() throws Exception {
        // Given
        List<CuotaDto> cuotasActivas = List.of(cuotaDto);
        when(cuotaService.obtenerCuotasActivasPorUsuario(1L)).thenReturn(cuotasActivas);

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/active/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].usuarioNombre").value("Juan Carlos García López"));
    }

    @Test
    @DisplayName("GET /api/v1/quotas/active - Debe retornar lista vacía cuando no hay cuotas activas")
    void shouldReturnEmptyListWhenNoActiveQuotas() throws Exception {
        // Given
        when(cuotaService.obtenerCuotasActivas()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/quotas/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}