package com.mas.co.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.CuotaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.CuotaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.impl.CuotaServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Tests unitarios para CuotaService.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Cuota Service Tests")
class CuotaServiceTest {

    @Mock
    private CuotaRepository cuotaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CuotaMapper cuotaMapper;

    @InjectMocks
    private CuotaServiceImpl cuotaService;

    private CuotaDto cuotaDto;
    private Cuota cuota;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Carlos")
                .apellidos("García López")
                .activo(true)
                .build();

        cuotaDto = CuotaDto.builder()
                .descripcion("Cuota mensual agua")
                .valorTotal(300000.0)
                .numeroCuota(12)
                .cuotaActual(0)
                .usuarioId(1L)
                .activo(true)
                .build();

        cuota = Cuota.builder()
                .id(1L)
                .descripcion("Cuota mensual agua")
                .valorCuota(25000.0) // Calculado automáticamente
                .valorTotal(300000.0)
                .numeroCuota(12)
                .cuotaActual(0)
                .activo(true)
                .usuario(usuario)
                .build();
    }

    @Test
    @DisplayName("Debe crear cuota exitosamente con cálculo automático")
    void shouldCreateQuotaSuccessfully() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(cuotaMapper.toEntity(cuotaDto)).thenReturn(cuota);
        when(cuotaRepository.save(any(Cuota.class))).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        CuotaDto resultado = cuotaService.crearCuota(cuotaDto);

        // Then
        assertThat(resultado).isNotNull();
        verify(cuotaRepository).save(any(Cuota.class));
        verify(usuarioRepository).findByIdAndActivoTrue(1L);
    }

    @Test
    @DisplayName("Debe fallar al crear cuota con usuario inexistente")
    void shouldFailWhenCreatingQuotaWithNonExistentUser() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cuotaService.crearCuota(cuotaDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Debe obtener cuota por ID")
    void shouldGetQuotaById() {
        // Given
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        CuotaDto resultado = cuotaService.obtenerCuota(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescripcion()).isEqualTo("Cuota mensual agua");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cuota no existe")
    void shouldThrowExceptionWhenQuotaNotFound() {
        // Given
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cuotaService.obtenerCuota(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cuota con ID 1 no encontrada");
    }

    @Test
    @DisplayName("Debe actualizar cuota exitosamente con recálculo")
    void shouldUpdateQuotaSuccessfully() {
        // Given
        CuotaDto updateDto = CuotaDto.builder()
                .descripcion("Cuota actualizada")
                .valorTotal(360000.0)
                .numeroCuota(12)
                .cuotaActual(2)
                .usuarioId(1L)
                .build();

        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(updateDto);

        // When
        CuotaDto resultado = cuotaService.actualizarCuota(1L, updateDto);

        // Then
        assertThat(resultado).isNotNull();
        verify(cuotaMapper).updateEntity(updateDto, cuota);
        verify(cuotaRepository).save(cuota);
    }

    @Test
    @DisplayName("Debe procesar pago de cuota exitosamente")
    void shouldProcessQuotaPaymentSuccessfully() {
        // Given
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        CuotaDto resultado = cuotaService.procesarPagoCuota(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(cuota.getCuotaActual()).isEqualTo(1); // Incrementado
        verify(cuotaRepository).save(cuota);
    }

    @Test
    @DisplayName("Debe desactivar cuota automáticamente al completarse")
    void shouldDeactivateQuotaWhenCompleted() {
        // Given
        cuota.setCuotaActual(11); // Penúltima cuota
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        cuotaService.procesarPagoCuota(1L);

        // Then
        assertThat(cuota.getCuotaActual()).isEqualTo(12); // Completada
        assertThat(cuota.getActivo()).isFalse(); // Desactivada automáticamente
    }

    @Test
    @DisplayName("Debe fallar al procesar pago de cuota inactiva")
    void shouldFailWhenProcessingPaymentOfInactiveQuota() {
        // Given
        cuota.setActivo(false);
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));

        // When & Then
        assertThatThrownBy(() -> cuotaService.procesarPagoCuota(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está activa para procesar pagos");
    }

    @Test
    @DisplayName("Debe desactivar cuota exitosamente")
    void shouldDeactivateQuotaSuccessfully() {
        // Given
        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));

        // When
        cuotaService.desactivarCuota(1L);

        // Then
        assertThat(cuota.getActivo()).isFalse();
        verify(cuotaRepository).save(cuota);
    }

    @Test
    @DisplayName("Debe eliminar cuota exitosamente")
    void shouldDeleteQuotaSuccessfully() {
        // Given
        when(cuotaRepository.existsById(1L)).thenReturn(true);

        // When
        cuotaService.eliminarCuota(1L);

        // Then
        verify(cuotaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe obtener cuotas paginadas")
    void shouldGetPaginatedQuotas() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cuota> cuotasPage = new PageImpl<>(List.of(cuota));
        
        when(cuotaRepository.findByActivoTrue(pageable)).thenReturn(cuotasPage);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        Page<CuotaDto> resultado = cuotaService.obtenerCuotas(pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener cuotas por usuario")
    void shouldGetQuotasByUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cuota> cuotasPage = new PageImpl<>(List.of(cuota));
        
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(cuotaRepository.findByUsuarioIdAndActivoTrue(1L, pageable)).thenReturn(cuotasPage);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        Page<CuotaDto> resultado = cuotaService.obtenerCuotasPorUsuario(1L, pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Debe buscar cuotas por término")
    void shouldSearchQuotasByTerm() {
        // Given
        String termino = "agua";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cuota> cuotasPage = new PageImpl<>(List.of(cuota));
        
        when(cuotaRepository.findByDescripcionContainingIgnoreCase(eq(termino), eq(pageable)))
                .thenReturn(cuotasPage);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaDto);

        // When
        Page<CuotaDto> resultado = cuotaService.buscarCuotas(termino, pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener cuotas activas")
    void shouldGetActiveQuotas() {
        // Given
        List<Cuota> cuotas = List.of(cuota);
        List<CuotaDto> cuotasDto = List.of(cuotaDto);
        
        when(cuotaRepository.findByActivoTrueOrderByFechaInsertDesc()).thenReturn(cuotas);
        when(cuotaMapper.toDtoList(cuotas)).thenReturn(cuotasDto);

        // When
        List<CuotaDto> resultado = cuotaService.obtenerCuotasActivas();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener cuotas activas por usuario")
    void shouldGetActiveQuotasByUser() {
        // Given
        List<Cuota> cuotas = List.of(cuota);
        List<CuotaDto> cuotasDto = List.of(cuotaDto);
        
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(cuotaRepository.findByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(1L)).thenReturn(cuotas);
        when(cuotaMapper.toDtoList(cuotas)).thenReturn(cuotasDto);

        // When
        List<CuotaDto> resultado = cuotaService.obtenerCuotasActivasPorUsuario(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cuota inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentQuota() {
        // Given
        when(cuotaRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> cuotaService.eliminarCuota(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cuota con ID 1 no encontrada");
    }

    @Test
    @DisplayName("Debe cambiar usuario al actualizar cuota con nuevo usuarioId")
    void shouldChangeUserWhenUpdatingQuotaWithNewUserId() {
        // Given
        Usuario nuevoUsuario = Usuario.builder()
                .id(2L)
                .nombre("María")
                .apellidos("González")
                .activo(true)
                .build();
        
        CuotaDto updateDto = CuotaDto.builder()
                .descripcion("Cuota actualizada")
                .valorTotal(360000.0)
                .numeroCuota(12)
                .cuotaActual(2)
                .usuarioId(2L) // Usuario diferente
                .build();

        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(usuarioRepository.findByIdAndActivoTrue(2L)).thenReturn(Optional.of(nuevoUsuario));
        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(updateDto);

        // When
        cuotaService.actualizarCuota(1L, updateDto);

        // Then
        verify(usuarioRepository).findByIdAndActivoTrue(2L);
        assertThat(cuota.getUsuario()).isEqualTo(nuevoUsuario);
    }

    @Test
    @DisplayName("Debe fallar al cambiar a usuario inexistente")
    void shouldFailWhenChangingToNonExistentUser() {
        // Given
        CuotaDto updateDto = CuotaDto.builder()
                .descripcion("Cuota actualizada")
                .valorTotal(360000.0)
                .numeroCuota(12)
                .cuotaActual(2)
                .usuarioId(999L) // Usuario inexistente
                .build();

        when(cuotaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(cuota));
        when(usuarioRepository.findByIdAndActivoTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cuotaService.actualizarCuota(1L, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 999 no encontrado");
    }
}