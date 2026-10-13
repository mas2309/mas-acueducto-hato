package com.mas.co.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Documento importante del acueducto (acta, contrato, resolución). El archivo vive en el
 * almacenamiento de objetos; aquí se guarda su dirección.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Entity
@Table(
    name = "documentos",
    schema = "acueducto",
    indexes = {@Index(name = "idx_documento_fecha_subida", columnList = "fecha_subida")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo", nullable = false, length = 100)
  private String titulo;

  @Column(name = "descripcion", length = 300)
  private String descripcion;

  @Column(name = "archivo_url", nullable = false, length = 500)
  private String archivoUrl;

  @Column(name = "archivo_nombre", nullable = false, length = 200)
  private String archivoNombre;

  @Column(name = "fecha_subida", nullable = false)
  private LocalDate fechaSubida;

  /** Usuario del panel que subió el documento. */
  @Column(name = "subido_por", nullable = false, length = 100)
  private String subidoPor;

  @PrePersist
  protected void onCreate() {
    if (fechaSubida == null) {
      fechaSubida = LocalDate.now();
    }
  }
}
