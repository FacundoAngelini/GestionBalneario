package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.EntregaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

public class PedidoReservaDTO {

    //@NotNull
    // private ReservaDTO reserva;

    //@NotNull
    // private PagoPedidoReservaDTO pagoPedidoReservaDTO;

    @NotNull
    private EntregaDTO entregaDTO;
}
