package com.mas.co.entity;

import com.mas.co.entity.enums.CategoriaGasto;
import com.mas.co.security.AdminUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "gastos",
    schema = "acueducto",
    indexes = {
        @Index(name = "idx_gasto_fecha", columnList = "fecha"),
        @Index(name = "idx_gasto_categoria", columnList = "categoria"),
        @Index(name = "idx_gasto_pagado", columnList = "pagado")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 30)
    private CategoriaGasto categoria;

    @Column(name = "pagado", nullable = false)
    @Builder.Default
    private Boolean pagado = false;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "soporte_url", length = 500)
    private String soporteUrl;

    @Column(name = "soporte_nombre", length = 200)
    private String soporteNombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por", nullable = false)
    private AdminUser registradoPor;

    @Column(name = "responsable", length = 100)
    private String responsable;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
    }
}
