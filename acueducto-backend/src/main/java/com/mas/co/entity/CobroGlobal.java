package com.mas.co.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cobro global: cuota extraordinaria que se aplica a todos los usuarios. Agrupa las cuotas
 * individuales generadas a cada usuario.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(name = "cobros_globales", schema = "acueducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CobroGlobal {

  @Id
  @EqualsAndHashCode.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "descripcion", nullable = false, length = 20)
  @NotBlank(message = "La descripción es obligatoria")
  private String descripcion;

  /** Valor total que aporta cada usuario. */
  @Column(name = "valor_total", nullable = false)
  @NotNull(message = "El valor total es obligatorio")
  @Positive(message = "El valor total debe ser positivo")
  private Double valorTotal;

  @Column(name = "numero_cuotas", nullable = false)
  @NotNull(message = "El número de cuotas es obligatorio")
  @Positive(message = "El número de cuotas debe ser positivo")
  private Integer numeroCuotas;

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDate fechaCreacion;

  @Column(name = "activo", nullable = false)
  @Builder.Default
  private Boolean activo = true;

  @PrePersist
  protected void onCreate() {
    if (fechaCreacion == null) {
      fechaCreacion = LocalDate.now();
    }
    if (activo == null) {
      activo = true;
    }
  }
}
