package com.Gestion.MiBalnearioGestion.Reservas.Entity;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/** Registro de una acción sobre una reserva. Solo se agregan filas: nunca se modifican ni se borran. */
@Entity
@Table(name = "reserva_evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaEntity reserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EAccionReserva accion;

    /** Nombre de usuario de quien hizo la acción ("sistema" si fue un proceso automático). */
    @Column(nullable = false)
    private String usuario;

    /** Ficha de empleado de quien hizo la acción (null si es un cliente o el admin supremo). */
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private EmpleadoEntity empleado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 300)
    private String motivo;

    @Column(length = 300)
    private String detalle;
}