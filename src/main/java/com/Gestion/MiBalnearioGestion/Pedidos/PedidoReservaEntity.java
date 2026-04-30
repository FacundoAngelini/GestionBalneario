package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name="pedidos_reserva")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoReservaEntity extends PedidoEntity{

    @ManyToOne
    @JoinColumn(name= "reserva_id", nullable = false)
    private ReservaEntity reserva;
}
