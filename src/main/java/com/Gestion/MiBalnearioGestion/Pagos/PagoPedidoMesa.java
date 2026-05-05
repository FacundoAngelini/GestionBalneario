package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Pedidos.PedidoMesaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pago_pedido_mesa")
public class PagoPedidoMesa extends PagoEntity {
    @OneToOne
    @JoinColumn(name="pedido_mesa_id", nullable = false)
    private PedidoMesaEntity pedidoMesa;
}
