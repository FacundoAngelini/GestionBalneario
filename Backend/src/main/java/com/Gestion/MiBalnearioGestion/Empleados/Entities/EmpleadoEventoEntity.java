package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/** Registro de una acción sobre la ficha de un empleado. Solo se agregan filas. */
@Entity
@Table(name = "empleado_evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EAccionEmpleado accion;

    /** Usuario de quien hizo la acción. */
    @Column(nullable = false)
    private String usuario;

    /** Ficha de quien hizo la acción (null si es el administrador supremo). */
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private EmpleadoEntity actor;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 300)
    private String motivo;

    @Column(length = 300)
    private String detalle;
}