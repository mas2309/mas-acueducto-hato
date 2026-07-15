package com.mas.co.entity;

import com.mas.co.constants.BusinessConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para la entidad Usuario.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Usuario Entity Tests")
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombre("Juan Carlos")
                .apellidos("García López")
                .build();
    }

    @Test
    @DisplayName("Debe crear usuario con valores por defecto")
    void shouldCreateUserWithDefaultValues() {
        // When
        usuario.onCreate();

        // Then
        assertThat(usuario.getFechaInsert()).isEqualTo(LocalDate.now());
        assertThat(usuario.getActivo()).isEqualTo(BusinessConstants.USER_ACTIVE);
    }

    @Test
    @DisplayName("Debe generar nombre completo correctamente")
    void shouldGenerateFullNameCorrectly() {
        // When
        String nombreCompleto = usuario.getNombreCompleto();

        // Then
        assertThat(nombreCompleto).isEqualTo("Juan Carlos García López");
    }

    @Test
    @DisplayName("Debe verificar si usuario está activo")
    void shouldVerifyIfUserIsActive() {
        // Given
        usuario.setActivo(true);

        // When
        boolean isActive = usuario.isActivo();

        // Then
        assertThat(isActive).isTrue();
    }

    @Test
    @DisplayName("Debe verificar si usuario está inactivo")
    void shouldVerifyIfUserIsInactive() {
        // Given
        usuario.setActivo(false);

        // When
        boolean isActive = usuario.isActivo();

        // Then
        assertThat(isActive).isFalse();
    }

    @Test
    @DisplayName("Debe manejar activo nulo como inactivo")
    void shouldHandleNullActiveAsFalse() {
        // Given
        usuario.setActivo(null);

        // When
        boolean isActive = usuario.isActivo();

        // Then
        assertThat(isActive).isFalse();
    }

    @Test
    @DisplayName("Debe mantener igualdad basada en nombre")
    void shouldMaintainEqualityBasedOnName() {
        // Given
        Usuario otroUsuario = Usuario.builder()
                .nombre("Juan Carlos")
                .apellidos("Diferentes Apellidos")
                .build();

        // When & Then
        assertThat(usuario).isEqualTo(otroUsuario);
        assertThat(usuario.hashCode()).isEqualTo(otroUsuario.hashCode());
    }
}