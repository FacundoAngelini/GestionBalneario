package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Empleados.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_pedido", nullable = false)
    private ETipoPedido tipoPedido;

    @OneToMany(mappedBy = "pedido")
    private List<DetallePedidoEntity> detallePedidos;

    @ManyToOne
    @JoinColumn(name="reserva_id", nullable = false)
    private ReservaEntity reserva;

    @ManyToMany
    @JoinTable(
            name="pedido_empleados",
            joinColumns = @JoinColumn(name="pedido_id"),
            inverseJoinColumns = @JoinColumn(name="empleado_id")
    )

    private List<EmpleadoEntity> empleados = new ArrayList<>();

    @OneToOne(mappedBy = "pago")
    private TicketEntity ticket;
}
