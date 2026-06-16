package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoLugarDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagoPedidoReservaDTO extends PagoDTO {

    @NotNull
    private PedidoLugarDTO pedidoLugarDTO;

    @NotNull
    private EntregaDTO entregaDTO;
}
