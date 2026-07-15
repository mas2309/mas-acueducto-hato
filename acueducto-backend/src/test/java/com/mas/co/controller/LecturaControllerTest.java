package com.mas.co.controller;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import com.mas.co.security.JwtService;
import com.mas.co.security.CustomUserDetailsService;
import com.mas.co.service.LecturaService;
import java.util.Collections;
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

@WebMvcTest(LecturaController.class)
@Import(TestSecurityConfig.class)
@DisplayName("LecturaController Tests")
class LecturaControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private LecturaService lecturaService;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private CustomUserDetailsService customUserDetailsService;

  private LecturaDto lecturaDto;
  private PagoDto pagoDto;

  @BeforeEach
  void setUp() {
    lecturaDto = LecturaDto.builder()
        .usuarioId(1L)
        .mes("Enero")
        .anio(2024)
        .lecturaActual(100)
        .lecturaAnterior(50)
        .consumo(50)
        .valorConsumo(10000.0)
        .cargoFijo(3000.0)
        .valorTotal(13000.0)
        .build();

    pagoDto = new PagoDto();
    pagoDto.setMetodoPago("EFECTIVO");
  }

  @Test
  @DisplayName("Ingresar lectura - Success")
  void ingresarLectura_Success() throws Exception {
    when(lecturaService.ingresarLectura(any(LecturaDto.class))).thenReturn(lecturaDto);

    mockMvc.perform(post("/api/v1/lecturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lecturaDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.usuarioId").value(1));

    verify(lecturaService).ingresarLectura(any(LecturaDto.class));
  }

  @Test
  @DisplayName("Registrar pago - Success")
  void registrarPago_Success() throws Exception {
    when(lecturaService.registrarPago(eq(1L), any(PagoDto.class))).thenReturn(lecturaDto);

    mockMvc.perform(patch("/api/v1/lecturas/1/pagar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pagoDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(lecturaService).registrarPago(eq(1L), any(PagoDto.class));
  }

  @Test
  @DisplayName("Obtener última lectura - Success")
  void obtenerUltimaLectura_Success() throws Exception {
    when(lecturaService.obtenerUltimaLectura(1L)).thenReturn(lecturaDto);

    mockMvc.perform(get("/api/v1/lecturas/ultima/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.usuarioId").value(1));

    verify(lecturaService).obtenerUltimaLectura(1L);
  }

  @Test
  @DisplayName("Obtener facturas por usuario - Success")
  void obtenerFacturasPorUsuario_Success() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.singletonList(lecturaDto));
    when(lecturaService.obtenerFacturasPorUsuario(eq(1L), any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/usuario/1")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.pagination.totalElements").value(1));

    verify(lecturaService).obtenerFacturasPorUsuario(eq(1L), any(PageRequest.class));
  }

  @Test
  @DisplayName("Obtener facturas por período - Success")
  void obtenerFacturasPorPeriodo_Success() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.singletonList(lecturaDto));
    when(lecturaService.obtenerFacturasPorPeriodo(eq("Enero"), eq(2024), any(PageRequest.class)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/periodo")
            .param("mes", "Enero")
            .param("anio", "2024")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray());

    verify(lecturaService).obtenerFacturasPorPeriodo(eq("Enero"), eq(2024), any(PageRequest.class));
  }

  @Test
  @DisplayName("Obtener facturas pendientes - Success")
  void obtenerFacturasPendientes_Success() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.singletonList(lecturaDto));
    when(lecturaService.obtenerFacturasPendientes(any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/pendientes")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray());

    verify(lecturaService).obtenerFacturasPendientes(any(PageRequest.class));
  }

  @Test
  @DisplayName("Obtener facturas pagadas - Success")
  void obtenerFacturasPagadas_Success() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.singletonList(lecturaDto));
    when(lecturaService.obtenerFacturasPagadas(any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/pagadas")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray());

    verify(lecturaService).obtenerFacturasPagadas(any(PageRequest.class));
  }

  @Test
  @DisplayName("Buscar facturas - Success")
  void buscarFacturas_Success() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.singletonList(lecturaDto));
    when(lecturaService.buscarFacturas(eq("Juan"), any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/search")
            .param("q", "Juan")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray());

    verify(lecturaService).buscarFacturas(eq("Juan"), any(PageRequest.class));
  }

  @Test
  @DisplayName("Obtener deuda pendiente - Success")
  void obtenerDeudaPendiente_Success() throws Exception {
    when(lecturaService.obtenerDeudaPendiente(1L)).thenReturn(50000.0);

    mockMvc.perform(get("/api/v1/lecturas/deuda/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").value(50000.0));

    verify(lecturaService).obtenerDeudaPendiente(1L);
  }

  @Test
  @DisplayName("Actualizar factura - Success")
  void actualizarFactura_Success() throws Exception {
    when(lecturaService.actualizarFactura(eq(1L), any(LecturaDto.class))).thenReturn(lecturaDto);

    mockMvc.perform(put("/api/v1/lecturas/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lecturaDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Factura actualizada exitosamente"));

    verify(lecturaService).actualizarFactura(eq(1L), any(LecturaDto.class));
  }

  @Test
  @DisplayName("Eliminar factura - Success")
  void eliminarFactura_Success() throws Exception {
    doNothing().when(lecturaService).eliminarFactura(1L);

    mockMvc.perform(delete("/api/v1/lecturas/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Factura eliminada exitosamente"));

    verify(lecturaService).eliminarFactura(1L);
  }

  @Test
  @DisplayName("Ingresar lectura - Validación falla")
  void ingresarLectura_ValidationFails() throws Exception {
    LecturaDto invalidDto = LecturaDto.builder().build();

    mockMvc.perform(post("/api/v1/lecturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    verify(lecturaService, never()).ingresarLectura(any());
  }

  @Test
  @DisplayName("Obtener facturas por usuario - Parámetros por defecto")
  void obtenerFacturasPorUsuario_DefaultParams() throws Exception {
    PageImpl<LecturaDto> page = new PageImpl<>(Collections.emptyList());
    when(lecturaService.obtenerFacturasPorUsuario(eq(1L), any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v1/lecturas/usuario/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(lecturaService).obtenerFacturasPorUsuario(eq(1L), any(PageRequest.class));
  }

  @Test
  @DisplayName("Obtener facturas pendientes - Página vacía")
  void obtenerFacturasPendientes_EmptyPage() throws Exception {
    PageImpl<LecturaDto> emptyPage = new PageImpl<>(Collections.emptyList());
    when(lecturaService.obtenerFacturasPendientes(any(PageRequest.class))).thenReturn(emptyPage);

    mockMvc.perform(get("/api/v1/lecturas/pendientes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.pagination.empty").value(true));

    verify(lecturaService).obtenerFacturasPendientes(any(PageRequest.class));
  }
}
