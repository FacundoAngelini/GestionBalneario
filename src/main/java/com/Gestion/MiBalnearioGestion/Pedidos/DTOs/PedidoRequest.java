package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PedidoRequest {



    @NotNull
    private ETipoPedido tipoPedido;

    @NotNull
    private List<DetallePedidoRequest> pedidos;
}
