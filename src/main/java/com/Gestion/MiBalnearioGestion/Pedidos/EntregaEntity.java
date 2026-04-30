package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Empleados.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="entregas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="estado_entrega", nullable = false)
    private boolean estadoEntrega;

    @OneToOne
    @JoinColumn(name="pedido_reserva_id", nullable = false)
    private PedidoEntity pedidoReserva;

    @ManyToOne
    @JoinColumn(name="empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    @OneToOne
    @JoinColumn(name="pago_pedido_reserva", nullable = false)
    private PagoPedidoReserva pagoPedidoReserva;
}
