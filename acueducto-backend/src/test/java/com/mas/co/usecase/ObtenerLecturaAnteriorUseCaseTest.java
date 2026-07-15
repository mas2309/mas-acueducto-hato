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
@DisplayName("Tests para ObtenerLecturaAnteriorUseCase")
class ObtenerLecturaAnteriorUseCaseTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private ObtenerLecturaAnteriorUseCase useCase;

  @Test
  @DisplayName("Debe retornar lectura actual de última factura")
  void debeRetornarLecturaActualDeUltimaFactura() {
    Factura factura = new Factura();
    factura.setLecturaActual(150);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura));

    Integer resultado = useCase.execute(1L);

    assertEquals(150, resultado);
  }

  @Test
  @DisplayName("Debe retornar cero cuando no hay facturas anteriores")
  void debeRetornarCeroCuandoNoHayFacturas() {
    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(Collections.emptyList());

    Integer resultado = useCase.execute(1L);

    assertEquals(0, resultado);
  }

  @Test
  @DisplayName("Debe considerar solo la primera factura")
  void debeConsiderarSoloPrimeraFactura() {
    Factura factura1 = new Factura();
    factura1.setLecturaActual(150);

    Factura factura2 = new Factura();
    factura2.setLecturaActual(100);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(factura1, factura2));

    Integer resultado = useCase.execute(1L);

    assertEquals(150, resultado);
  }
}
