package com.mas.co.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import java.util.Collections;
import java.util.List;
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

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    Double deuda = useCase.execute(1L);

    assertEquals(50000.0, deuda);
  }

  @Test
  @DisplayName("Debe retornar cero cuando última factura está pagada con pago")
  void debeRetornarCeroCuandoUltimaFacturaPagadaConPago() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(true);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    Double deuda = useCase.execute(1L);

    assertEquals(0.0, deuda);
  }

  @Test
  @DisplayName("Debe retornar cero cuando última factura está pagada con pagoBanco")
  void debeRetornarCeroCuandoUltimaFacturaPagadaConPagoBanco() {
    Factura factura = new Factura();
    factura.setValorTotal(50000.0);
    factura.setPago(false);
    factura.setPagoBanco(true);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    Double deuda = useCase.execute(1L);

    assertEquals(0.0, deuda);
  }

  @Test
  @DisplayName("Debe retornar cero cuando no hay facturas anteriores")
  void debeRetornarCeroCuandoNoHayFacturasAnteriores() {
    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(Collections.emptyList());

    Double deuda = useCase.execute(1L);

    assertEquals(0.0, deuda);
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura de la lista")
  void debeConsiderarSoloPrimeraFactura() {
    Factura factura1 = new Factura();
    factura1.setValorTotal(50000.0);
    factura1.setPago(false);
    factura1.setPagoBanco(false);

    Factura factura2 = new Factura();
    factura2.setValorTotal(30000.0);
    factura2.setPago(false);
    factura2.setPagoBanco(false);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura1, factura2));

    Double deuda = useCase.execute(1L);

    assertEquals(50000.0, deuda);
  }
}
