package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoMesaDTO;
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
public class PagoPedidoMesaDTO extends PagoDTO {

    @NotNull
    private PedidoMesaDTO pedidoMesaDTO;
}
