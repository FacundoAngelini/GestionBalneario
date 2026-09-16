package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoMesaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="pago_pedido_mesa")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class PagoPedidoMesaEntity extends PagoEntity {
    @OneToOne
    @JoinColumn(name="pedido_mesa_id", nullable = false)
    private PedidoMesaEntity pedidoMesa;
}
