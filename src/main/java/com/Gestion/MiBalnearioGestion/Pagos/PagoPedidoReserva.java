package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Pedidos.EntregaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.PedidoReservaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pago_pedido_reserva")
public class PagoPedidoReserva extends PagoEntity {

    @OneToOne
    @JoinColumn(name="pedido_reserva_id", nullable = false)
    private PedidoReservaEntity pedidoReserva;

    @OneToOne(mappedBy = "pagoPedidoReserva")
    private EntregaEntity entrega;
}
