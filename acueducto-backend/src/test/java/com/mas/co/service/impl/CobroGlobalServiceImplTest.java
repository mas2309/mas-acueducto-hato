package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.CobroGlobalDto;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.entity.CobroGlobal;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.exception.ValidationException;
import com.mas.co.mapper.CobroGlobalMapper;
import com.mas.co.mapper.CuotaMapper;
import com.mas.co.mapper.UsuarioMapper;
import com.mas.co.repository.CobroGlobalRepository;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CobroGlobalServiceImpl")
class CobroGlobalServiceImplTest {

  @Mock private CobroGlobalRepository cobroGlobalRepository;

  @Mock private CuotaRepository cuotaRepository;

  @Mock private UsuarioRepository usuarioRepository;

  @Spy private CobroGlobalMapper cobroGlobalMapper;

  @Spy private CuotaMapper cuotaMapper;

  @Spy private UsuarioMapper usuarioMapper;

  @Captor private ArgumentCaptor<List<Cuota>> cuotasCaptor;

  @InjectMocks private CobroGlobalServiceImpl cobroGlobalService;

  private Usuario juan;
  private Usuario maria;
  private CobroGlobal cobroGlobal;
  private CobroGlobalDto cobroGlobalDto;

  @BeforeEach
  void setUp() {
    juan = Usuario.builder().id(1L).nombre("Juan").apellidos("Pérez").build();
    maria = Usuario.builder().id(2L).nombre("María").apellidos("Gómez").build();

    cobroGlobal =
        CobroGlobal.builder()
            .id(10L)
            .descripcion("Obras tanque")
            .valorTotal(100000.0)
            .numeroCuotas(2)
            .build();

    cobroGlobalDto =
        CobroGlobalDto.builder()
            .descripcion("Obras tanque")
            .valorTotal(100000.0)
            .numeroCuotas(2)
            .build();
  }

  @Test
  @DisplayName("Debe crear el cobro y una cuota por cada usuario activo")
  void debeCrearCobroConCuotaPorUsuarioActivo() {
    when(usuarioRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of(juan, maria));
    when(cobroGlobalRepository.save(any(CobroGlobal.class))).thenReturn(cobroGlobal);
    when(cuotaRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    CobroGlobalDto resultado = cobroGlobalService.crearCobroGlobal(cobroGlobalDto);

    verify(cuotaRepository).saveAll(cuotasCaptor.capture());
    List<Cuota> cuotas = cuotasCaptor.getValue();
    assertEquals(2, cuotas.size());
    Cuota cuotaMaria = cuotas.get(1);
    assertSame(maria, cuotaMaria.getUsuario());
    assertSame(cobroGlobal, cuotaMaria.getCobroGlobal());
    assertEquals("Obras tanque", cuotaMaria.getDescripcion());
    assertEquals(100000.0, cuotaMaria.getValorTotal());
    assertEquals(50000.0, cuotaMaria.getValorCuota());
    assertEquals(2, cuotaMaria.getNumeroCuota());
    assertEquals(0, cuotaMaria.getCuotaActual());
    assertTrue(cuotaMaria.getActivo());
    assertFalse(cuotaMaria.getPagoAnticipado());

    assertEquals(10L, resultado.getId());
    assertEquals(2, resultado.getTotalUsuarios());
    assertEquals(200000.0, resultado.getValorEsperado());
    assertEquals(200000.0, resultado.getValorPendiente());
  }

  @Test
  @DisplayName("No debe crear el cobro si no hay usuarios activos")
  void noDebeCrearCobroSinUsuariosActivos() {
    when(usuarioRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of());

    assertThrows(
        BusinessException.class, () -> cobroGlobalService.crearCobroGlobal(cobroGlobalDto));

    verify(cobroGlobalRepository, never()).save(any());
  }

  @Test
  @DisplayName("No debe crear el cobro con datos inválidos")
  void noDebeCrearCobroConDatosInvalidos() {
    cobroGlobalDto.setNumeroCuotas(0);

    assertThrows(
        ValidationException.class, () -> cobroGlobalService.crearCobroGlobal(cobroGlobalDto));

    verify(cobroGlobalRepository, never()).save(any());
  }

  @Test
  @DisplayName("Debe calcular el resumen de avance del cobro")
  void debeCalcularResumenDelCobro() {
    Cuota enCobro = cuota(juan, 1, true, false);
    Cuota anticipada = cuota(maria, 0, false, true);
    Cuota completada = cuota(juan, 2, false, false);
    Cuota desactivada = cuota(maria, 1, false, false);
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(cuotaRepository.findByCobroGlobalId(10L))
        .thenReturn(List.of(enCobro, anticipada, completada, desactivada));

    CobroGlobalDto resultado = cobroGlobalService.obtenerCobroGlobal(10L);

    assertEquals(50000.0, resultado.getValorCuota());
    assertEquals(4, resultado.getTotalUsuarios());
    assertEquals(1, resultado.getUsuariosEnCobro());
    assertEquals(1, resultado.getUsuariosPagoAnticipado());
    assertEquals(1, resultado.getUsuariosCompletados());
    assertEquals(1, resultado.getUsuariosDesactivados());
    assertEquals(400000.0, resultado.getValorEsperado());
    assertEquals(200000.0, resultado.getValorFacturado());
    assertEquals(100000.0, resultado.getValorAnticipado());
    assertEquals(50000.0, resultado.getValorPendiente());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el cobro no existe")
  void debeLanzarExcepcionSiCobroNoExiste() {
    when(cobroGlobalRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> cobroGlobalService.obtenerCobroGlobal(99L));
  }

  @Test
  @DisplayName("Debe listar solo los usuarios activos sin cuota en el cobro")
  void debeListarUsuariosSinCobro() {
    when(cuotaRepository.findByCobroGlobalId(10L)).thenReturn(List.of(cuota(juan, 0, true, false)));
    when(usuarioRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of(juan, maria));

    List<UsuarioDto> resultado = cobroGlobalService.obtenerUsuariosSinCobro(10L);

    assertEquals(1, resultado.size());
    assertEquals(2L, resultado.get(0).getId());
  }

  @Test
  @DisplayName("Debe aplicar el cobro a un usuario nuevo")
  void debeAgregarUsuarioAlCobro() {
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(usuarioRepository.findByIdAndActivoTrue(2L)).thenReturn(Optional.of(maria));
    when(cuotaRepository.existsByCobroGlobalIdAndUsuarioId(10L, 2L)).thenReturn(false);
    when(cuotaRepository.save(any(Cuota.class))).thenAnswer(invocation -> invocation.getArgument(0));

    cobroGlobalService.agregarUsuario(10L, 2L);

    ArgumentCaptor<Cuota> cuotaCaptor = ArgumentCaptor.forClass(Cuota.class);
    verify(cuotaRepository).save(cuotaCaptor.capture());
    assertSame(maria, cuotaCaptor.getValue().getUsuario());
    assertSame(cobroGlobal, cuotaCaptor.getValue().getCobroGlobal());
    assertEquals(50000.0, cuotaCaptor.getValue().getValorCuota());
  }

  @Test
  @DisplayName("No debe aplicar el cobro dos veces al mismo usuario")
  void noDebeAgregarUsuarioQueYaTieneElCobro() {
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(juan));
    when(cuotaRepository.existsByCobroGlobalIdAndUsuarioId(10L, 1L)).thenReturn(true);

    assertThrows(BusinessException.class, () -> cobroGlobalService.agregarUsuario(10L, 1L));

    verify(cuotaRepository, never()).save(any());
  }

  @Test
  @DisplayName("No debe aplicar a un usuario un cobro cancelado")
  void noDebeAgregarUsuarioACobroCancelado() {
    cobroGlobal.setActivo(false);
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));

    assertThrows(BusinessException.class, () -> cobroGlobalService.agregarUsuario(10L, 2L));

    verify(cuotaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Cancelar debe desactivar solo las cuotas activas")
  void cancelarDebeDesactivarCuotasActivas() {
    Cuota enCobro = cuota(juan, 1, true, false);
    Cuota anticipada = cuota(maria, 0, false, true);
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(cuotaRepository.findByCobroGlobalId(10L)).thenReturn(List.of(enCobro, anticipada));

    cobroGlobalService.cancelarCobroGlobal(10L);

    assertFalse(enCobro.getActivo());
    assertTrue(anticipada.getPagoAnticipado());
    assertFalse(cobroGlobal.getActivo());
    verify(cuotaRepository).saveAll(List.of(enCobro));
    verify(cobroGlobalRepository).save(cobroGlobal);
  }

  @Test
  @DisplayName("Debe eliminar el cobro si ninguna cuota se ha facturado")
  void debeEliminarCobroSinFacturar() {
    List<Cuota> cuotas = List.of(cuota(juan, 0, true, false), cuota(maria, 0, true, false));
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(cuotaRepository.findByCobroGlobalId(10L)).thenReturn(cuotas);

    cobroGlobalService.eliminarCobroGlobal(10L);

    verify(cuotaRepository).deleteAll(cuotas);
    verify(cobroGlobalRepository).delete(cobroGlobal);
  }

  @Test
  @DisplayName("No debe eliminar el cobro si ya tiene cuotas facturadas")
  void noDebeEliminarCobroFacturado() {
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(cuotaRepository.findByCobroGlobalId(10L))
        .thenReturn(List.of(cuota(juan, 1, true, false), cuota(maria, 0, true, false)));

    assertThrows(BusinessException.class, () -> cobroGlobalService.eliminarCobroGlobal(10L));

    verify(cobroGlobalRepository, never()).delete(any());
  }

  @Test
  @DisplayName("No debe eliminar el cobro si tiene pagos anticipados")
  void noDebeEliminarCobroConPagoAnticipado() {
    when(cobroGlobalRepository.findById(10L)).thenReturn(Optional.of(cobroGlobal));
    when(cuotaRepository.findByCobroGlobalId(10L))
        .thenReturn(List.of(cuota(juan, 0, false, true)));

    assertThrows(BusinessException.class, () -> cobroGlobalService.eliminarCobroGlobal(10L));

    verify(cobroGlobalRepository, never()).delete(any());
  }

  private Cuota cuota(Usuario usuario, int cuotaActual, boolean activo, boolean pagoAnticipado) {
    return Cuota.builder()
        .descripcion("Obras tanque")
        .valorTotal(100000.0)
        .valorCuota(50000.0)
        .numeroCuota(2)
        .cuotaActual(cuotaActual)
        .activo(activo)
        .pagoAnticipado(pagoAnticipado)
        .usuario(usuario)
        .cobroGlobal(cobroGlobal)
        .build();
  }
}
