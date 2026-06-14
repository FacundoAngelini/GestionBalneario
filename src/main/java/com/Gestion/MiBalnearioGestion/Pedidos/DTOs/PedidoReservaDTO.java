package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.Entrega.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter @Setter @NoArgsConstructor
@SuperBuilder
public class PedidoReservaDTO extends PedidoRequest {
    @NotNull
    private UUID reservaId;
}
