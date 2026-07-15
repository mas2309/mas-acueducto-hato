package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.CuotaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.CuotaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CuotaServiceImpl")
class CuotaServiceImplTest {

  @Mock private CuotaRepository cuotaRepository;

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private CuotaMapper cuotaMapper;

  @InjectMocks private CuotaServiceImpl cuotaService;

  private Usuario usuario;
  private Cuota cuota;
  private CuotaDto cuotaDto;

  @BeforeEach
  void setUp() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Juan Pérez");

    cuota = new Cuota();
    cuota.setId(1L);
    cuota.setUsuario(usuario);
    cuota.setDescripcion("Cuota test");
    cuota.setValorTotal(100000.0);
    cuota.setNumeroCuota(10);
    cuota.setValorCuota(10000.0);
    cuota.setCuotaActual(0);
    cuota.setActivo(true);

    cuotaDto = new CuotaDto();
    cuotaDto.setId(1L);
    cuotaDto.setUsuarioId(1L);
    cuotaDto.setDescripcion("Cuota test");
    cuotaDto.setValorTotal(100000.0);
    cuotaDto.setNumeroCuota(10);
    cuotaDto.setValorCuota(10000.0);
  }

  @Test
  @DisplayName("Debe crear cuota exitosamente")
  void debeCrearCuotaExitosamente() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
    when(cuotaMapper.toEntity(cuotaDto)).thenReturn(cuota);
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    CuotaDto resultado = cuotaService.crearCuota(cuotaDto);

    assertNotNull(resultado);
    assertEquals(10000.0, cuota.getValorCuota());
    verify(cuotaRepository).save(any(Cuota.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción al crear cuota con usuario inexistente")
  void debeLanzarExcepcionAlCrearCuotaConUsuarioInexistente() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> cuotaService.crearCuota(cuotaDto));
    verify(cuotaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Debe obtener cuota por ID")
  void debeObtenerCuotaPorId() {
    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    CuotaDto resultado = cuotaService.obtenerCuota(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Debe lanzar excepción al obtener cuota inexistente")
  void debeLanzarExcepcionAlObtenerCuotaInexistente() {
    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> cuotaService.obtenerCuota(1L));
  }

  @Test
  @DisplayName("Debe actualizar cuota exitosamente")
  void debeActualizarCuotaExitosamente() {
    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    CuotaDto resultado = cuotaService.actualizarCuota(1L, cuotaDto);

    assertNotNull(resultado);
    verify(cuotaRepository).save(cuota);
  }

  @Test
  @DisplayName("Debe actualizar cuota cambiando usuario")
  void debeActualizarCuotaCambiandoUsuario() {
    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setId(2L);

    cuotaDto.setUsuarioId(2L);

    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(usuarioRepository.findByIdAndActivoTrue(2L)).thenReturn(Optional.of(nuevoUsuario));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    CuotaDto resultado = cuotaService.actualizarCuota(1L, cuotaDto);

    assertNotNull(resultado);
    assertEquals(nuevoUsuario, cuota.getUsuario());
  }

  @Test
  @DisplayName("Debe lanzar excepción al actualizar con usuario inexistente")
  void debeLanzarExcepcionAlActualizarConUsuarioInexistente() {
    cuotaDto.setUsuarioId(2L);

    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(usuarioRepository.findByIdAndActivoTrue(2L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> cuotaService.actualizarCuota(1L, cuotaDto));
  }

  @Test
  @DisplayName("Debe procesar pago de cuota exitosamente")
  void debeProcesarPagoCuotaExitosamente() {
    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    CuotaDto resultado = cuotaService.procesarPagoCuota(1L);

    assertNotNull(resultado);
    assertEquals(1, cuota.getCuotaActual());
    assertTrue(cuota.getActivo());
  }

  @Test
  @DisplayName("Debe desactivar cuota al completar todos los pagos")
  void debeDesactivarCuotaAlCompletarPagos() {
    cuota.setCuotaActual(9);

    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
    when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

    cuotaService.procesarPagoCuota(1L);

    assertEquals(10, cuota.getCuotaActual());
    assertFalse(cuota.getActivo());
  }

  @Test
  @DisplayName("Debe lanzar excepción al procesar pago de cuota inactiva")
  void debeLanzarExcepcionAlProcesarPagoCuotaInactiva() {
    cuota.setActivo(false);

    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));

    assertThrows(BusinessException.class, () -> cuotaService.procesarPagoCuota(1L));
  }

  @Test
  @DisplayName("Debe desactivar cuota exitosamente")
  void debeDesactivarCuotaExitosamente() {
    when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
    when(cuotaRepository.save(cuota)).thenReturn(cuota);

    cuotaService.desactivarCuota(1L);

    assertFalse(cuota.getActivo());
    verify(cuotaRepository).save(cuota);
  }

  @Test
  @DisplayName("Debe eliminar cuota exitosamente")
  void debeEliminarCuotaExitosamente() {
    when(cuotaRepository.existsById(1L)).thenReturn(true);

    cuotaService.eliminarCuota(1L);

    verify(cuotaRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Debe lanzar excepción al eliminar cuota inexistente")
  void debeLanzarExcepcionAlEliminarCuotaInexistente() {
    when(cuotaRepository.existsById(1L)).thenReturn(false);

    assertThrows(BusinessException.class, () -> cuotaService.eliminarCuota(1L));
    verify(cuotaRepository, never()).deleteById(anyLong());
  }
}
