package com.mas.co.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
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
@DisplayName("Tests para VerificarPagoLecturaAnteriorUseCase")
class VerificarPagoLecturaAnteriorUseCaseTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private VerificarPagoLecturaAnteriorUseCase useCase;

  @Test
  @DisplayName("Debe retornar true cuando última factura está pagada con pago")
  void debeRetornarTrueCuandoUltimaFacturaPagadaConPago() {
    Factura factura = new Factura();
    factura.setPago(true);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    boolean resultado = useCase.execute(1L);

    assertTrue(resultado);
  }

  @Test
  @DisplayName("Debe retornar true cuando última factura está pagada con pagoBanco")
  void debeRetornarTrueCuandoUltimaFacturaPagadaConPagoBanco() {
    Factura factura = new Factura();
    factura.setPago(false);
    factura.setPagoBanco(true);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    boolean resultado = useCase.execute(1L);

    assertTrue(resultado);
  }

  @Test
  @DisplayName("Debe retornar false cuando última factura no está pagada")
  void debeRetornarFalseCuandoUltimaFacturaNoPagada() {
    Factura factura = new Factura();
    factura.setPago(false);
    factura.setPagoBanco(false);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    boolean resultado = useCase.execute(1L);

    assertFalse(resultado);
  }

  @Test
  @DisplayName("Debe retornar true cuando no hay facturas anteriores")
  void debeRetornarTrueCuandoNoHayFacturasAnteriores() {
    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(Collections.emptyList());

    boolean resultado = useCase.execute(1L);

    assertTrue(resultado);
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura de la lista")
  void debeConsiderarSoloPrimeraFactura() {
    Factura factura1 = new Factura();
    factura1.setPago(false);
    factura1.setPagoBanco(false);

    Factura factura2 = new Factura();
    factura2.setPago(true);
    factura2.setPagoBanco(false);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura1, factura2));

    boolean resultado = useCase.execute(1L);

    assertFalse(resultado);
  }

  @Test
  @DisplayName("Debe invocar el repositorio con el usuarioId correcto")
  void debeInvocarRepositorioConUsuarioIdCorrecto() {
    when(facturaRepository.findUltimaLecturaPorUsuario(999L)).thenReturn(Collections.emptyList());

    useCase.execute(999L);

    verify(facturaRepository).findUltimaLecturaPorUsuario(999L);
  }
}
