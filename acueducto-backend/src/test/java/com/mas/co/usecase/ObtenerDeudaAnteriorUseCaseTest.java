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
@DisplayName("Tests para ObtenerDeudaAnteriorUseCase")
class ObtenerDeudaAnteriorUseCaseTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private ObtenerDeudaAnteriorUseCase useCase;

  @Test
  @DisplayName("Debe retornar valor total cuando última factura no está pagada")
  void debeRetornarValorTotalCuandoUltimaFacturaNoPagada() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(false);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(50000.0, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar cero cuando última factura está pagada con pago")
  void debeRetornarCeroCuandoUltimaFacturaPagadaConPago() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(true);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(0.0, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar cero cuando última factura está pagada con pagoBanco")
  void debeRetornarCeroCuandoUltimaFacturaPagadaConPagoBanco() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(false);
    factura.setPagoBanco(true);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(0.0, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar cero cuando no hay facturas anteriores")
  void debeRetornarCeroCuandoNoHayFacturasAnteriores() {
    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.empty());

    assertEquals(0.0, useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura de la lista")
  void debeConsiderarSoloPrimeraFactura() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(false);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaFacturaPorUsuario(1L)).thenReturn(Optional.of(factura));

    assertEquals(50000.0, useCase.execute(1L));
  }
}
