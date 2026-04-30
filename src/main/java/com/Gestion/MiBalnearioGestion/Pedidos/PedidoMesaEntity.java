package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Recursos.MesaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="pedido_mesa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMesaEntity extends PedidoEntity {

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private MesaEntity mesa;
}
