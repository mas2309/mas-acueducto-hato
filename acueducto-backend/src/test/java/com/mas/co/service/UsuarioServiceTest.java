package com.mas.co.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.UsuarioDto;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.UsuarioMapper;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.impl.UsuarioServiceImpl;
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
 * Tests unitarios para UsuarioService.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Usuario Service Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioDto usuarioDto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDto = UsuarioDto.builder()
                .nombre("Juan Carlos")
                .apellidos("García López")
                .activo(true)
                .build();

        usuario = Usuario.builder()
                .nombre("Juan Carlos")
                .apellidos("García López")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe crear usuario exitosamente")
    void shouldCreateUserSuccessfully() {
        // Given
        when(usuarioMapper.toEntity(usuarioDto)).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDto);

        // When
        UsuarioDto resultado = usuarioService.crearUsuario(usuarioDto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Carlos");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe obtener usuario por ID")
    void shouldGetUserById() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDto);

        // When
        UsuarioDto resultado = usuarioService.obtenerUsuario(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Juan Carlos");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.obtenerUsuario(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Debe actualizar usuario exitosamente")
    void shouldUpdateUserSuccessfully() {
        // Given
        UsuarioDto updateDto = UsuarioDto.builder()
                .nombre("Juan Carlos Actualizado")
                .apellidos("García López")
                .build();

        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDto(usuario)).thenReturn(updateDto);

        // When
        UsuarioDto resultado = usuarioService.actualizarUsuario(1L, updateDto);

        // Then
        assertThat(resultado).isNotNull();
        verify(usuarioMapper).updateEntity(updateDto, usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe desactivar usuario exitosamente")
    void shouldDeactivateUserSuccessfully() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));

        // When
        usuarioService.desactivarUsuario(1L);

        // Then
        assertThat(usuario.getActivo()).isFalse();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe obtener usuarios paginados")
    void shouldGetPaginatedUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findByActivoTrue(pageable)).thenReturn(usuariosPage);
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDto);

        // When
        Page<UsuarioDto> resultado = usuarioService.obtenerUsuarios(pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).isEqualTo("Juan Carlos");
    }

    @Test
    @DisplayName("Debe buscar usuarios por término")
    void shouldSearchUsersByTerm() {
        // Given
        String termino = "Juan";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(List.of(usuario));
        
        when(usuarioRepository.findByNombreOrApellidosContainingIgnoreCase(eq(termino), eq(termino), eq(pageable)))
                .thenReturn(usuariosPage);
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDto);

        // When
        Page<UsuarioDto> resultado = usuarioService.buscarUsuarios(termino, pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener usuarios activos")
    void shouldGetActiveUsers() {
        // Given
        List<Usuario> usuarios = List.of(usuario);
        List<UsuarioDto> usuariosDto = List.of(usuarioDto);
        
        when(usuarioRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(usuarios);
        when(usuarioMapper.toDtoList(usuarios)).thenReturn(usuariosDto);

        // When
        List<UsuarioDto> resultado = usuarioService.obtenerUsuariosActivos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan Carlos");
    }

    @Test
    @DisplayName("Debe eliminar usuario exitosamente")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.eliminarUsuario(1L);

        // Then
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar usuario inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> usuarioService.eliminarUsuario(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar usuario inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.actualizarUsuario(1L, usuarioDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción al desactivar usuario inexistente")
    void shouldThrowExceptionWhenDeactivatingNonExistentUser() {
        // Given
        when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> usuarioService.desactivarUsuario(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuario con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando no hay usuarios")
    void shouldReturnEmptyPageWhenNoUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> emptyPage = new PageImpl<>(List.of());
        
        when(usuarioRepository.findByActivoTrue(pageable)).thenReturn(emptyPage);

        // When
        Page<UsuarioDto> resultado = usuarioService.obtenerUsuarios(pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay usuarios activos")
    void shouldReturnEmptyListWhenNoActiveUsers() {
        // Given
        when(usuarioRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of());
        when(usuarioMapper.toDtoList(List.of())).thenReturn(List.of());

        // When
        List<UsuarioDto> resultado = usuarioService.obtenerUsuariosActivos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
    }
}