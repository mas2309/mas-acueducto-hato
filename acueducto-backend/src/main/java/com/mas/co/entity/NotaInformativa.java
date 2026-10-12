package com.mas.co.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Nota informativa que se imprime en los recibos mientras esté activa.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(name = "notas_informativas", schema = "acueducto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaInformativa {

  public static final int MAX_LONGITUD = 150;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "texto", nullable = false, length = MAX_LONGITUD)
  private String texto;

  @Column(name = "activa", nullable = false)
  private Boolean activa;

  @Column(name = "fecha_actualizacion", nullable = false)
  private LocalDate fechaActualizacion;
}
