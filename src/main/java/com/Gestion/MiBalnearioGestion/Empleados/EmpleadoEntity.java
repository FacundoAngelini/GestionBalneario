package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Pedidos.PedidoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="estado_empleado", nullable = false)
    private boolean estadoEmpleado;

    @Column(name="apellido", nullable = false)
    private String apellido;

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

    @ManyToMany(mappedBy = "empleado")
    private List<PedidoEntity> pedidos = new ArrayList<>();



}
