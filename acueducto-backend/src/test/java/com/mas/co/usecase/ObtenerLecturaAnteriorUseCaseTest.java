package com.mas.co.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ObtenerLecturaAnteriorUseCase")
class ObtenerLecturaAnteriorUseCaseTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private ObtenerLecturaAnteriorUseCase useCase;

  @Test
  @DisplayName("Debe retornar lectura actual de última factura")
  void debeRetornarLecturaActualDeUltimaFactura() {
    Factura factura = new Factura();
    factura.setLecturaActual(150);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(150, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar cero cuando no hay facturas anteriores")
  void debeRetornarCeroCuandoNoHayFacturas() {
    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.empty());

    assertEquals(0, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura")
  void debeConsiderarSoloPrimeraFactura() {
    Factura factura = new Factura();
    factura.setLecturaActual(150);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(150, useCase.execute(1L));
  }
}
