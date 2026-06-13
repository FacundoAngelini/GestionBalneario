package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.Entrega.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import jakarta.validation.constraints.NotNull;

public class PedidoReservaDTO {

    @NotNull
     private ReservaDTO reserva;

    @NotNull
     private PagoPedidoReservaDTO pagoPedidoReservaDTO;

    @NotNull
    private EntregaDTO entregaDTO;
}
