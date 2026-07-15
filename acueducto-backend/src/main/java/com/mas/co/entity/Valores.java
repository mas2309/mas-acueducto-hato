package com.mas.co.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Valores para configuración de tarifas del sistema.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "valores", schema = "acueducto")
public class Valores implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cargo_fijo")
  private Double cargoFijo;

  @Column(name = "rango_uno")
  private Double rangoUno;

  @Column(name = "rango_dos")
  private Double rangoDos;

  @Column(name = "rango_tres")
  private Double rangoTres;

  @Column(name = "rango_cuatro")
  private Double rangoCuatro;

  @Column(name = "valor_uno")
  private Integer valorUno;

  @Column(name = "valor_dos")
  private Integer valorDos;

  @Column(name = "valor_tres")
  private Integer valorTres;

  @Column(name = "valor_cuatro")
  private Integer valorCuatro;

  @Column(name = "no_pago")
  private Double noPago;
}
