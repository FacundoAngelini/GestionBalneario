package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoReservaDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagoPedidoReservaDTO extends PagoDTO {

    @NotNull
    private PedidoReservaDTO pedidoReservaDTO;

    @NotNull
    private EntregaDTO entregaDTO;
}
