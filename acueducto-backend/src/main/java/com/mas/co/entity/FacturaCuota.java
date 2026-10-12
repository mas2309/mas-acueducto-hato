package com.mas.co.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuota cobrada en una factura. Guarda una copia de la descripción, el número de cuota y el valor
 * para que la factura no cambie si la cuota se edita después.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(name = "factura_cuotas", schema = "acueducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaCuota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "factura_id", nullable = false)
  private Factura factura;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cuota_id", nullable = false)
  private Cuota cuota;

  @Column(name = "descripcion", nullable = false, length = 20)
  private String descripcion;

  /** Número de la cuota cobrada. Nulo en facturas anteriores al detalle de cuotas. */
  @Column(name = "numero_cuota")
  private Integer numeroCuota;

  @Column(name = "total_cuotas", nullable = false)
  private Integer totalCuotas;

  @Column(name = "valor", nullable = false)
  private Double valor;
}
