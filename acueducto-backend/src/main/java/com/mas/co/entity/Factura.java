package com.mas.co.entity;

import com.mas.co.constants.BusinessConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
 * Entidad Factura del sistema de acueducto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(
    name = "facturas",
    schema = "acueducto",
    indexes = {
      @Index(name = "idx_factura_usuario_mes_anio", columnList = "usuario_id, mes, anio"),
      @Index(name = "idx_factura_fecha_ingreso", columnList = "fecha_ingreso"),
      @Index(name = "idx_factura_pago", columnList = "pago, pago_banco")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Factura {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @EqualsAndHashCode.Include
  @Column(name = "fecha_ingreso", nullable = false)
  @NotNull(message = "La fecha de ingreso es obligatoria")
  private LocalDate fechaIngreso;

  @Column(name = "fecha_pago")
  private LocalDate fechaPago;

  @Column(name = "fecha_actualizacion")
  private LocalDate fechaActualizacion;

  @Column(name = "mes", nullable = false, length = 20)
  @NotNull(message = "El mes es obligatorio")
  private String mes;

  @Column(name = "anio", nullable = false)
  @NotNull(message = "El año es obligatorio")
  private Integer anio;

  @Column(name = "lectura_actual", nullable = false)
  @NotNull(message = "La lectura actual es obligatoria")
  @PositiveOrZero(message = "La lectura actual debe ser positiva")
  private Integer lecturaActual;

  @Column(name = "lectura_anterior", nullable = false)
  @NotNull(message = "La lectura anterior es obligatoria")
  @PositiveOrZero(message = "La lectura anterior debe ser positiva")
  private Integer lecturaAnterior;

  @Column(name = "consumo", nullable = false)
  @Builder.Default
  private Integer consumo = BusinessConstants.DEFAULT_CONSUMPTION;

  @Column(name = "valor_consumo", nullable = false)
  @Builder.Default
  private Double valorConsumo = BusinessConstants.DEFAULT_DEBT;

  @Column(name = "valor_total", nullable = false)
  @Builder.Default
  private Double valorTotal = BusinessConstants.DEFAULT_DEBT;

  @Column(name = "otros_cobros")
  @Builder.Default
  private Double otrosCobros = BusinessConstants.DEFAULT_OTHER_CHARGES;

  @Column(name = "otros_cobros_descripcion", length = 100)
  private String otrosCobrosDescripcion;

  @Column(name = "deuda_anterior")
  @Builder.Default
  private Double deudaAnterior = BusinessConstants.DEFAULT_DEBT;

  @Column(name = "valor_cuota")
  @Builder.Default
  private Double valorCuota = BusinessConstants.DEFAULT_DEBT;

  @Column(name = "cargo_fijo")
  @Builder.Default
  private Double cargoFijo = BusinessConstants.DEFAULT_DEBT;

  @Column(name = "pago")
  @Builder.Default
  private Boolean pago = false;

  @Column(name = "pago_banco")
  @Builder.Default
  private Boolean pagoBanco = false;

  @Column(name = "no_pago")
  @Builder.Default
  private Double noPago = BusinessConstants.DEFAULT_DEBT;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  @NotNull(message = "El usuario es obligatorio")
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cuota_id")
  private Cuota cuota;

  @PrePersist
  protected void onCreate() {
    if (fechaIngreso == null) {
      fechaIngreso = LocalDate.now();
    }
    if (consumo == null) {
      consumo = BusinessConstants.DEFAULT_CONSUMPTION;
    }
    if (pago == null) {
      pago = false;
    }
    if (pagoBanco == null) {
      pagoBanco = false;
    }

    // Validar que no existan ambos pagos simultáneamente
    if (pago && pagoBanco) {
      throw new IllegalStateException("No se puede tener pago y pagoBanco activos simultáneamente");
    }
  }

  /** Calcula el consumo basado en las lecturas. */
  public void calcularConsumo() {
    if (lecturaActual != null && lecturaAnterior != null) {
      this.consumo = lecturaActual - lecturaAnterior;
    }
  }

  /**
   * Verifica si la factura está pagada.
   *
   * @return true si está pagada por cualquier medio
   */
  public boolean isPagada() {
    return Boolean.TRUE.equals(pago) || Boolean.TRUE.equals(pagoBanco);
  }

  /**
   * Obtiene el estado de pago como string.
   *
   * @return estado de pago
   */
  public String getEstadoPago() {
    if (Boolean.TRUE.equals(pago)) {
      return BusinessConstants.PAYMENT_TYPE_CASH;
    } else if (Boolean.TRUE.equals(pagoBanco)) {
      return BusinessConstants.PAYMENT_TYPE_BANK;
    } else {
      return BusinessConstants.PAYMENT_TYPE_NO_PAYMENT;
    }
  }

  /** Setter personalizado para pago que asegura exclusividad. */
  public void setPago(Boolean pago) {
    if (Boolean.TRUE.equals(pago) && Boolean.TRUE.equals(this.pagoBanco)) {
      this.pagoBanco = false;
    }
    this.pago = pago;
  }

  /** Setter personalizado para pagoBanco que asegura exclusividad. */
  public void setPagoBanco(Boolean pagoBanco) {
    if (Boolean.TRUE.equals(pagoBanco) && Boolean.TRUE.equals(this.pago)) {
      this.pago = false;
    }
    this.pagoBanco = pagoBanco;
  }
}
