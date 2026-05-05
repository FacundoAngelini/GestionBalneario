package com.Gestion.MiBalnearioGestion.Pedidos.Entity;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
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
    private PagoPedidoMesaEntity pagoPedidoMesa;
}
