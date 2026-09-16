package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoLugarEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pago_pedido_lugar")
@Getter @Setter @NoArgsConstructor @SuperBuilder @AllArgsConstructor
public class PagoPedidoLugarEntity extends PagoEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoLugarEntity pedido;
    @Column(name = "preference_id_mp")
    private String preferenceIdMp;
}