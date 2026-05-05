package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="precio_recursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrecioRecursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="precio", nullable = false)
    private double precio;

    @Column(name="fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia;

    @Column(name="fecha_caducada", nullable = false)
    private LocalDate fechaCaducada;

    @ManyToOne
    @JoinColumn(name="recurso_id", nullable = false)
    private RecursoEntity recurso;
}
