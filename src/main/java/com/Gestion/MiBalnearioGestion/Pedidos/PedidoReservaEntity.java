package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
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
public class PedidoReservaEntity extends PedidoEntity{

    @ManyToOne
    @JoinColumn(name= "reserva_id", nullable = false)
    private ReservaEntity reserva;

    @OneToOne(mappedBy = "pedido_reserva")
    private PagoPedidoReserva pagoPedidoReserva;

    @OneToOne(mappedBy = "entrega_id")
    private EntregaEntity entrega;
}
