package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="empleados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name="dni", unique = true, nullable = false)
    private int dni;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_empleado", nullable = false)
    private EEstadoEmpleado estadoEmpleado;

    @Column(name="apellido", nullable = false)
    private String apellido;

    @Embedded
    @Column(name="direccion", nullable = false)
    private DireccionEntity direccion;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    @Column(name="cuit", nullable = false)
    private String cuit;

    @Column(name="sueldo", nullable = false)
    private double sueldo;

    @ManyToOne
    @JoinColumn(name="sector_id", nullable = false)
    private SectorEntity sector;

    @ManyToOne
    @JoinColumn(name="rol_id", nullable = false)
    private RolEntity rol;

    @ManyToMany(mappedBy = "empleados")
    private List<PedidoEntity> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "empleado")
    private List<TicketEntity> tickets = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="usuario_id", nullable= false)
    private UsuarioEntity usuario;

}
