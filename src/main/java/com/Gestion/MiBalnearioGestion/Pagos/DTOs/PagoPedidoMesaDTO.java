package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoMesaDTO;
import jakarta.validation.constraints.NotNull;

public class PagoPedidoMesaDTO {

    @NotNull
    private PedidoMesaDTO pedidoMesaDTO;
}
