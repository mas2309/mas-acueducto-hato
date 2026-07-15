package com.mas.co.entity;

import com.mas.co.constants.BusinessConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad Usuario del sistema de acueducto.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(name = "usuarios", schema = "acueducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "nombres", nullable = false, length = 20)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH, 
          message = "El nombre debe tener entre 4 y 20 caracteres")
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 20)
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(min = BusinessConstants.MIN_NAME_LENGTH, max = BusinessConstants.MAX_NAME_LENGTH, 
          message = "Los apellidos deben tener entre 4 y 20 caracteres")
    private String apellidos;

    @Column(name = "fecha_insert", nullable = false)
    private LocalDate fechaInsert;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = BusinessConstants.USER_ACTIVE;

    @PrePersist
    protected void onCreate() {
        if (fechaInsert == null) {
            fechaInsert = LocalDate.now();
        }
        if (activo == null) {
            activo = BusinessConstants.USER_ACTIVE;
        }
    }

    /**
     * Obtiene el nombre completo del usuario.
     * 
     * @return nombre completo
     */
    public String getNombreCompleto() {
        return String.format("%s %s", nombre, apellidos);
    }

    /**
     * Verifica si el usuario está activo.
     * 
     * @return true si está activo
     */
    public boolean isActivo() {
        return Boolean.TRUE.equals(activo);
    }
}