package com.mas.co.entity;

import com.mas.co.constants.BusinessConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad Cuota del sistema de acueducto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(name = "cuotas", schema = "acueducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cuota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @EqualsAndHashCode.Include
  @Column(name = "descripcion", nullable = false, length = 20)
  @NotBlank(message = "La descripción es obligatoria")
  private String descripcion;

  @Column(name = "valor", nullable = false)
  @NotNull(message = "El valor de la cuota es obligatorio")
  @PositiveOrZero(message = "El valor debe ser positivo")
  private Double valorCuota;

  @Column(name = "valor_total", nullable = false)
  @NotNull(message = "El valor total es obligatorio")
  @PositiveOrZero(message = "El valor total debe ser positivo")
  private Double valorTotal;

  @Column(name = "fecha_insert", nullable = false)
  private LocalDate fechaInsert;

  @Column(name = "numero_cuota", nullable = false)
  @NotNull(message = "El número de cuotas es obligatorio")
  private Integer numeroCuota;

  @Column(name = "cuota_actual", nullable = false)
  @NotNull(message = "La cuota actual es obligatoria")
  private Integer cuotaActual;

  @Column(name = "activo", nullable = false)
  @Builder.Default
  private Boolean activo = BusinessConstants.QUOTA_ACTIVE;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "usuario_id", nullable = false)
  @NotNull(message = "El usuario es obligatorio")
  private Usuario usuario;

  @PrePersist
  protected void onCreate() {
    if (fechaInsert == null) {
      fechaInsert = LocalDate.now();
    }
    if (activo == null) {
      activo = BusinessConstants.QUOTA_ACTIVE;
    }
  }

  /**
   * Verifica si la cuota está activa.
   *
   * @return true si está activa
   */
  public boolean isActiva() {
    return Boolean.TRUE.equals(activo);
  }
}
