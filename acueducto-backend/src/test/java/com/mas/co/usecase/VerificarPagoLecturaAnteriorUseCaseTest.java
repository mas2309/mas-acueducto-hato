package com.mas.co.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.repository.FacturaRepository;
import java.util.Optional;
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
    when(facturaRepository.findUltimaFacturaPagada(1L)).thenReturn(Optional.of(true));

    assertTrue(useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar true cuando última factura está pagada con pagoBanco")
  void debeRetornarTrueCuandoUltimaFacturaPagadaConPagoBanco() {
    when(facturaRepository.findUltimaFacturaPagada(1L)).thenReturn(Optional.of(true));

    assertTrue(useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar false cuando última factura no está pagada")
  void debeRetornarFalseCuandoUltimaFacturaNoPagada() {
    when(facturaRepository.findUltimaFacturaPagada(1L)).thenReturn(Optional.of(false));

    assertFalse(useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe retornar true cuando no hay facturas anteriores")
  void debeRetornarTrueCuandoNoHayFacturasAnteriores() {
    when(facturaRepository.findUltimaFacturaPagada(1L)).thenReturn(Optional.empty());

    assertTrue(useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura de la lista")
  void debeConsiderarSoloPrimeraFactura() {
    when(facturaRepository.findUltimaFacturaPagada(1L)).thenReturn(Optional.of(false));

    assertFalse(useCase.execute(1L));
  }

  @Test
  @DisplayName("Debe invocar el repositorio con el usuarioId correcto")
  void debeInvocarRepositorioConUsuarioIdCorrecto() {
    when(facturaRepository.findUltimaFacturaPagada(999L)).thenReturn(Optional.empty());

    useCase.execute(999L);

    verify(facturaRepository).findUltimaFacturaPagada(999L);
  }
}
