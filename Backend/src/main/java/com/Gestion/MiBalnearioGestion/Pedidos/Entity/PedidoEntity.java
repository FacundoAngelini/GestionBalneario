package com.Gestion.MiBalnearioGestion.Pedidos.Entity;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name="fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_pedido", nullable = false)
    private ETipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_pedido", nullable = false)
    private EEstadoPedido estadoPedido;
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedidoEntity> detallePedidos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="pedido_empleados",
            joinColumns = @JoinColumn(name="pedido_id"),
            inverseJoinColumns = @JoinColumn(name="empleado_id")
    )
    private List<EmpleadoEntity> empleados = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    private ClienteEntity cliente;

}
