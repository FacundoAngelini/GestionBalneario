package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;


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
