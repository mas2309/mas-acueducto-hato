package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.entity.Valores;
import com.mas.co.repository.ValorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValorServiceImpl Tests")
class ValorServiceImplTest {

  @Mock private ValorRepository valorRepository;
  @InjectMocks private ValorServiceImpl valorService;

  private Valores valores;

  @BeforeEach
  void setUp() {
    valores = new Valores();
    valores.setId(1L);
    valores.setCargoFijo(3000.0);
    valores.setRangoUno(10.0);
    valores.setValorUno(600);
    valores.setRangoDos(20.0);
    valores.setValorDos(1000);
    valores.setRangoTres(30.0);
    valores.setValorTres(1500);
    valores.setRangoCuatro(31.0);
    valores.setValorCuatro(3500);
    valores.setNoPago(2000.0);
  }

  @Test
  @DisplayName("Obtener valores - Desde BD")
  void obtenerValores_DesdeBD_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Valores result = valorService.obtenerValores();

    assertNotNull(result);
    assertEquals(3000.0, result.getCargoFijo());
    verify(valorRepository).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Obtener valores - Por defecto cuando no hay en BD")
  void obtenerValores_PorDefecto_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

    Valores result = valorService.obtenerValores();

    assertNotNull(result);
    assertEquals(3000.0, result.getCargoFijo());
    assertEquals(10.0, result.getRangoUno());
    assertEquals(600, result.getValorUno());
    verify(valorRepository).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Calcular valor consumo - Consumo null")
  void calcularValorConsumo_ConsumoNull_ReturnsZero() {
    Double result = valorService.calcularValorConsumo(null);

    assertEquals(0.0, result);
    verify(valorRepository, never()).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Calcular valor consumo - Consumo cero")
  void calcularValorConsumo_ConsumoCero_ReturnsZero() {
    Double result = valorService.calcularValorConsumo(0);

    assertEquals(0.0, result);
    verify(valorRepository, never()).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Calcular valor consumo - Consumo negativo")
  void calcularValorConsumo_ConsumoNegativo_ReturnsZero() {
    Double result = valorService.calcularValorConsumo(-5);

    assertEquals(0.0, result);
    verify(valorRepository, never()).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Calcular valor consumo - Rango 1 (5 m³)")
  void calcularValorConsumo_Rango1_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(5);

    assertEquals(3000.0, result); // 5 * 600
    verify(valorRepository).findFirstByOrderByIdDesc();
  }

  @Test
  @DisplayName("Calcular valor consumo - Límite Rango 1 (10 m³)")
  void calcularValorConsumo_LimiteRango1_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(10);

    assertEquals(6000.0, result); // 10 * 600
  }

  @Test
  @DisplayName("Calcular valor consumo - Rango 2 (15 m³)")
  void calcularValorConsumo_Rango2_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(15);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 5 * 1000 = 5000
    // Total: 11000
    assertEquals(11000.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Límite Rango 2 (20 m³)")
  void calcularValorConsumo_LimiteRango2_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(20);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 10 * 1000 = 10000
    // Total: 16000
    assertEquals(16000.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Rango 3 (25 m³)")
  void calcularValorConsumo_Rango3_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(25);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 10 * 1000 = 10000
    // Rango 3: 5 * 1500 = 7500
    // Total: 23500
    assertEquals(23500.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Límite Rango 3 (30 m³)")
  void calcularValorConsumo_LimiteRango3_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(30);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 10 * 1000 = 10000
    // Rango 3: 10 * 1500 = 15000
    // Total: 31000
    assertEquals(31000.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Rango 4 (35 m³)")
  void calcularValorConsumo_Rango4_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(35);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 10 * 1000 = 10000
    // Rango 3: 10 * 1500 = 15000
    // Rango 4: 5 * 3500 = 17500
    // Total: 48500
    assertEquals(48500.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Rango 4 alto (50 m³)")
  void calcularValorConsumo_Rango4Alto_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(50);

    // Rango 1: 10 * 600 = 6000
    // Rango 2: 10 * 1000 = 10000
    // Rango 3: 10 * 1500 = 15000
    // Rango 4: 20 * 3500 = 70000
    // Total: 101000
    assertEquals(101000.0, result);
  }

  @Test
  @DisplayName("Calcular valor consumo - Consumo 1 m³")
  void calcularValorConsumo_UnMetroCubico_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(valores));

    Double result = valorService.calcularValorConsumo(1);

    assertEquals(600.0, result); // 1 * 600
  }

  @Test
  @DisplayName("Calcular valor consumo - Usa valores por defecto")
  void calcularValorConsumo_UsaValoresPorDefecto_Success() {
    when(valorRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

    Double result = valorService.calcularValorConsumo(5);

    assertEquals(3000.0, result); // 5 * 600 (valores por defecto)
  }
}
