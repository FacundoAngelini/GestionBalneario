package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoReservaDTO;
import jakarta.validation.constraints.NotNull;

public class PagoPedidoReservaDTO {

    @NotNull
    private PedidoReservaDTO pedidoReservaDTO;

    @NotNull
    private EntregaDTO entregaDTO;
}
