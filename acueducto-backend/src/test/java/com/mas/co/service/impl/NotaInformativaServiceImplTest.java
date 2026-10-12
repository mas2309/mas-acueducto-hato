package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.NotaInformativaDto;
import com.mas.co.entity.NotaInformativa;
import com.mas.co.exception.ValidationException;
import com.mas.co.repository.NotaInformativaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para NotaInformativaServiceImpl")
class NotaInformativaServiceImplTest {

  @Mock private NotaInformativaRepository notaInformativaRepository;

  @InjectMocks private NotaInformativaServiceImpl notaInformativaService;

  private NotaInformativa nota;

  @BeforeEach
  void setUp() {
    nota =
        NotaInformativa.builder()
            .id(1L)
            .texto("Corte de agua el lunes")
            .activa(true)
            .fechaActualizacion(LocalDate.of(2026, 3, 1))
            .build();
  }

  @Test
  @DisplayName("Debe devolver una nota vacía e inactiva si no hay ninguna")
  void debeDevolverNotaVaciaSiNoHayNinguna() {
    when(notaInformativaRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

    NotaInformativaDto resultado = notaInformativaService.obtenerNota();

    assertEquals("", resultado.getTexto());
    assertFalse(resultado.getActiva());
    assertEquals("", notaInformativaService.obtenerTextoVigente());
  }

  @Test
  @DisplayName("Debe devolver el texto vigente solo si la nota está activa")
  void debeDevolverTextoVigenteSoloSiEstaActiva() {
    when(notaInformativaRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(nota));

    assertEquals("Corte de agua el lunes", notaInformativaService.obtenerTextoVigente());

    nota.setActiva(false);

    assertEquals("", notaInformativaService.obtenerTextoVigente());
  }

  @Test
  @DisplayName("Debe actualizar la nota existente dejándola en un solo párrafo")
  void debeActualizarNotaExistente() {
    when(notaInformativaRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(nota));
    when(notaInformativaRepository.save(any(NotaInformativa.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    NotaInformativaDto resultado =
        notaInformativaService.actualizarNota(
            NotaInformativaDto.builder().texto("  Asamblea general\r\nel 20 de abril  ").activa(true).build());

    assertEquals("Asamblea general el 20 de abril", resultado.getTexto());
    assertTrue(resultado.getActiva());
    assertEquals(LocalDate.now(), resultado.getFechaActualizacion());
    assertEquals(1L, nota.getId());
    verify(notaInformativaRepository).save(nota);
  }

  @Test
  @DisplayName("Debe crear la nota si no existe y dejarla inactiva si no se marca")
  void debeCrearNotaInactiva() {
    when(notaInformativaRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());
    when(notaInformativaRepository.save(any(NotaInformativa.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    NotaInformativaDto resultado =
        notaInformativaService.actualizarNota(NotaInformativaDto.builder().texto("Borrador").build());

    assertNotNull(resultado);
    assertEquals("Borrador", resultado.getTexto());
    assertFalse(resultado.getActiva());
  }

  @Test
  @DisplayName("No debe activar una nota sin texto")
  void noDebeActivarNotaSinTexto() {
    NotaInformativaDto dto = NotaInformativaDto.builder().texto("   ").activa(true).build();

    assertThrows(ValidationException.class, () -> notaInformativaService.actualizarNota(dto));

    verify(notaInformativaRepository, never()).save(any());
  }

  @Test
  @DisplayName("No debe guardar una nota de más de 150 caracteres")
  void noDebeGuardarNotaMuyLarga() {
    NotaInformativaDto dto = NotaInformativaDto.builder().texto("a".repeat(151)).activa(true).build();

    assertThrows(ValidationException.class, () -> notaInformativaService.actualizarNota(dto));

    verify(notaInformativaRepository, never()).save(any());
  }
}
