package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.LecturaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.LecturaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.CuotaService;
import com.mas.co.usecase.CalcularValoresFacturaUseCase;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LecturaServiceImpl - Actualizar y Eliminar Tests")
class LecturaServiceImplUpdateDeleteTest {

  @Mock private FacturaRepository facturaRepository;
  @Mock private CuotaRepository cuotaRepository;
  @Mock private LecturaMapper lecturaMapper;
  @Mock private CuotaService cuotaService;
  @Mock private CalcularValoresFacturaUseCase calcularValoresFacturaUseCase;

  @InjectMocks private LecturaServiceImpl lecturaService;

  private Factura factura;
  private Usuario usuario;
  private LecturaDto lecturaDto;
  private Cuota cuota;

  @BeforeEach
  void setUp() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Test");

    factura = new Factura();
    factura.setId(1L);
    factura.setUsuario(usuario);
    factura.setMes("Enero");
    factura.setAnio(2024);
    factura.setLecturaActual(100);
    factura.setLecturaAnterior(50);
    factura.setPago(false);
    factura.setPagoBanco(false);

    lecturaDto =
        LecturaDto.builder()
            .usuarioId(1L)
            .mes("Enero")
            .anio(2024)
            .lecturaActual(120)
            .lecturaAnterior(50)
            .valorConsumo(10000.0)
            .cargoFijo(3000.0)
            .otrosCobros(0.0)
            .deudaAnterior(0.0)
            .valorCuota(0.0)
            .build();

    cuota = new Cuota();
    cuota.setId(1L);
    cuota.setCuotaActual(5);
    cuota.setActivo(true);
  }

  @Test
  @DisplayName("Actualizar factura exitosamente sin cambio de cuota")
  void actualizarFactura_SinCambioCuota_Success() {
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
    when(lecturaMapper.toDto(any(Factura.class))).thenReturn(lecturaDto);
    doNothing().when(calcularValoresFacturaUseCase).execute(any());

    LecturaDto result = lecturaService.actualizarFactura(1L, lecturaDto);

    assertNotNull(result);
    verify(facturaRepository).findById(1L);
    verify(facturaRepository).save(any(Factura.class));
    verify(calcularValoresFacturaUseCase).execute(any());
  }

  @Test
  @DisplayName("Actualizar factura - Factura no encontrada")
  void actualizarFactura_FacturaNoEncontrada_ThrowsException() {
    when(facturaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> lecturaService.actualizarFactura(1L, lecturaDto));

    verify(facturaRepository).findById(1L);
    verify(facturaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Actualizar factura - Cambio de usuario no permitido")
  void actualizarFactura_CambioUsuario_ThrowsException() {
    lecturaDto.setUsuarioId(2L);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

    assertThrows(BusinessException.class, () -> lecturaService.actualizarFactura(1L, lecturaDto));

    verify(facturaRepository).findById(1L);
    verify(facturaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Actualizar factura - Período duplicado")
  void actualizarFactura_PeriodoDuplicado_ThrowsException() {
    lecturaDto.setMes("Febrero");
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.existsByUsuarioIdAndMesAndAnio(1L, "Febrero", 2024)).thenReturn(true);

    assertThrows(BusinessException.class, () -> lecturaService.actualizarFactura(1L, lecturaDto));

    verify(facturaRepository).existsByUsuarioIdAndMesAndAnio(1L, "Febrero", 2024);
  }

  @Test
  @DisplayName("Actualizar factura - Lectura inválida")
  void actualizarFactura_LecturaInvalida_ThrowsException() {
    lecturaDto.setLecturaActual(30);
    lecturaDto.setLecturaAnterior(50);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

    assertThrows(BusinessException.class, () -> lecturaService.actualizarFactura(1L, lecturaDto));
  }

  @Test
  @DisplayName("Actualizar factura - Agregar cuota")
  void actualizarFactura_AgregarCuota_Success() {
    lecturaDto.setCuotaId(1L);
    lecturaDto.setValorCuota(25000.0);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
    when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
    when(lecturaMapper.toDto(any(Factura.class))).thenReturn(lecturaDto);
    doNothing().when(calcularValoresFacturaUseCase).execute(any());

    LecturaDto result = lecturaService.actualizarFactura(1L, lecturaDto);

    assertNotNull(result);
    verify(cuotaRepository).findById(1L);
    verify(cuotaService).procesarPagoCuota(1L);
  }

  @Test
  @DisplayName("Actualizar factura - Cambiar cuota")
  void actualizarFactura_CambiarCuota_Success() {
    Cuota cuotaAnterior = new Cuota();
    cuotaAnterior.setId(2L);
    cuotaAnterior.setCuotaActual(3);
    factura.setCuota(cuotaAnterior);

    lecturaDto.setCuotaId(1L);
    lecturaDto.setValorCuota(25000.0);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuotaAnterior);
    when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
    when(lecturaMapper.toDto(any(Factura.class))).thenReturn(lecturaDto);
    doNothing().when(calcularValoresFacturaUseCase).execute(any());

    LecturaDto result = lecturaService.actualizarFactura(1L, lecturaDto);

    assertNotNull(result);
    verify(cuotaRepository).save(cuotaAnterior);
    assertEquals(2, cuotaAnterior.getCuotaActual());
  }

  @Test
  @DisplayName("Actualizar factura - Quitar cuota")
  void actualizarFactura_QuitarCuota_Success() {
    factura.setCuota(cuota);
    lecturaDto.setCuotaId(null);

    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
    when(lecturaMapper.toDto(any(Factura.class))).thenReturn(lecturaDto);
    doNothing().when(calcularValoresFacturaUseCase).execute(any());

    LecturaDto result = lecturaService.actualizarFactura(1L, lecturaDto);

    assertNotNull(result);
    verify(cuotaRepository).save(cuota);
    assertEquals(4, cuota.getCuotaActual());
  }

  @Test
  @DisplayName("Actualizar factura - Cuota no encontrada")
  void actualizarFactura_CuotaNoEncontrada_ThrowsException() {
    lecturaDto.setCuotaId(99L);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(cuotaRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> lecturaService.actualizarFactura(1L, lecturaDto));
  }

  @Test
  @DisplayName("Actualizar factura - Factura pagada preserva estado")
  void actualizarFactura_FacturaPagada_PreservaEstado() {
    factura.setPago(true);
    lecturaDto.setNoPago(2000.0);

    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.save(any(Factura.class))).thenReturn(factura);
    when(lecturaMapper.toDto(any(Factura.class))).thenReturn(lecturaDto);
    doNothing().when(calcularValoresFacturaUseCase).execute(any());

    LecturaDto result = lecturaService.actualizarFactura(1L, lecturaDto);

    assertNotNull(result);
    assertEquals(0.0, lecturaDto.getNoPago());
    assertTrue(lecturaDto.getPago());
  }

  @Test
  @DisplayName("Eliminar factura exitosamente sin cuota")
  void eliminarFactura_SinCuota_Success() {
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    doNothing().when(facturaRepository).deleteById(1L);

    assertDoesNotThrow(() -> lecturaService.eliminarFactura(1L));

    verify(facturaRepository).findById(1L);
    verify(facturaRepository).deleteById(1L);
    verify(cuotaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Eliminar factura con cuota - Revierte cuota")
  void eliminarFactura_ConCuota_RevierteCuota() {
    factura.setCuota(cuota);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    doNothing().when(facturaRepository).deleteById(1L);

    lecturaService.eliminarFactura(1L);

    verify(facturaRepository).findById(1L);
    verify(cuotaRepository).save(cuota);
    verify(facturaRepository).deleteById(1L);
    assertEquals(4, cuota.getCuotaActual());
    assertTrue(cuota.getActivo());
  }

  @Test
  @DisplayName("Eliminar factura - Factura no encontrada")
  void eliminarFactura_FacturaNoEncontrada_ThrowsException() {
    when(facturaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> lecturaService.eliminarFactura(1L));

    verify(facturaRepository).findById(1L);
    verify(facturaRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("Eliminar factura - Cuota con cuotaActual en 0 no se revierte")
  void eliminarFactura_CuotaEnCero_NoRevierte() {
    cuota.setCuotaActual(0);
    factura.setCuota(cuota);
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    doNothing().when(facturaRepository).deleteById(1L);

    lecturaService.eliminarFactura(1L);

    verify(cuotaRepository, never()).save(any());
    verify(facturaRepository).deleteById(1L);
  }
}
