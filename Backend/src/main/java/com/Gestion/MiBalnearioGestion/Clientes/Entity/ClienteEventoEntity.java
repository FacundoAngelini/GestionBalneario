package com.Gestion.MiBalnearioGestion.Clientes.Entity;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/** Registro de una acción sobre la ficha de un cliente. Solo se agregan filas. */
@Entity
@Table(name = "cliente_evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private EAccionCliente accion;

    /** Usuario de quien hizo la acción. */
    @Column(nullable = false)
    private String usuario;

    /** Ficha de empleado de quien actuó (null si fue el propio cliente o el administrador supremo). */
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