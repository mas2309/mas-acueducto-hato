package com.mas.co.usecase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mas.co.dto.LecturaDto;
import com.mas.co.entity.Factura;
import com.mas.co.exception.BusinessException;
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
@DisplayName("Tests para ValidarPeriodoConsecutivoUseCase")
class ValidarPeriodoConsecutivoUseCaseTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private ValidarPeriodoConsecutivoUseCase useCase;

  @Test
  @DisplayName("Debe validar correctamente período consecutivo dentro del mismo año")
  void debeValidarPeriodoConsecutivoMismoAnio() {
    Factura ultimaFactura = new Factura();
    ultimaFactura.setMes("Enero");
    ultimaFactura.setAnio(2024);

    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Febrero");
    dto.setAnio(2024);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

    assertDoesNotThrow(() -> useCase.execute(dto));
  }

  @Test
  @DisplayName("Debe validar correctamente período consecutivo cambiando de año")
  void debeValidarPeriodoConsecutivoCambioAnio() {
    Factura ultimaFactura = new Factura();
    ultimaFactura.setMes("Diciembre");
    ultimaFactura.setAnio(2023);

    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Enero");
    dto.setAnio(2024);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

    assertDoesNotThrow(() -> useCase.execute(dto));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el mes no es consecutivo")
  void debeLanzarExcepcionCuandoMesNoConsecutivo() {
    Factura ultimaFactura = new Factura();
    ultimaFactura.setMes("Enero");
    ultimaFactura.setAnio(2024);

    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Marzo");
    dto.setAnio(2024);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

    assertThrows(BusinessException.class, () -> useCase.execute(dto));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el año no es consecutivo")
  void debeLanzarExcepcionCuandoAnioNoConsecutivo() {
    Factura ultimaFactura = new Factura();
    ultimaFactura.setMes("Diciembre");
    ultimaFactura.setAnio(2023);

    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Enero");
    dto.setAnio(2025);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

    assertThrows(BusinessException.class, () -> useCase.execute(dto));
  }

  @Test
  @DisplayName("Debe permitir cualquier período cuando no hay facturas anteriores")
  void debePermitirCualquierPeriodoSinFacturasAnteriores() {
    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Marzo");
    dto.setAnio(2024);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(Collections.emptyList());

    assertDoesNotThrow(() -> useCase.execute(dto));
  }

  @Test
  @DisplayName("Debe validar todos los meses del año correctamente")
  void debeValidarTodosLosMesesDelAnio() {
    String[] meses = {
      "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
      "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    for (int i = 0; i < meses.length - 1; i++) {
      Factura ultimaFactura = new Factura();
      ultimaFactura.setMes(meses[i]);
      ultimaFactura.setAnio(2024);

      LecturaDto dto = new LecturaDto();
      dto.setUsuarioId(1L);
      dto.setMes(meses[i + 1]);
      dto.setAnio(2024);

      when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

      assertDoesNotThrow(
          () -> useCase.execute(dto),
          "Debe validar correctamente de " + meses[i] + " a " + meses[i + 1]);
    }
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando se intenta repetir el mismo período")
  void debeLanzarExcepcionCuandoSeRepiteMismoPeriodo() {
    Factura ultimaFactura = new Factura();
    ultimaFactura.setMes("Enero");
    ultimaFactura.setAnio(2024);

    LecturaDto dto = new LecturaDto();
    dto.setUsuarioId(1L);
    dto.setMes("Enero");
    dto.setAnio(2024);

    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of(ultimaFactura));

    assertThrows(BusinessException.class, () -> useCase.execute(dto));
  }
}
