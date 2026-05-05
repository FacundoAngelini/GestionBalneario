package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Pagos.PagoPedidoMesa;
import com.Gestion.MiBalnearioGestion.Recursos.MesaEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="pedido_mesa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PedidoMesaEntity extends PedidoEntity {

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private MesaEntity mesa;

    @OneToOne(mappedBy = "pedidoMesa")
    private PagoPedidoMesa pagoPedidoMesa;
}
