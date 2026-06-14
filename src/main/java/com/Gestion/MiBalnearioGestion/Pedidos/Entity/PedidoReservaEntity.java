package com.Gestion.MiBalnearioGestion.Pedidos.Entity;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entrega.EntregaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name="pedidos_reserva")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PedidoReservaEntity extends PedidoEntity {

    @ManyToOne
    @JoinColumn(name= "reserva_id", nullable = false)
    private ReservaEntity reserva;

    @OneToOne(mappedBy = "pedidoReserva")
    private PagoPedidoReservaEntity pagoPedidoReserva;


}
