package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter @Setter @NoArgsConstructor @SuperBuilder
public class PedidoLugarDTO extends PedidoRequest {

    @NotNull(message = "Debe indicar el recurso (carpa o sombrilla)")
    private UUID recursoPublicId;

    @NotNull(message = "Debe indicar el cliente")
    private UUID clienteId;
    private UUID empleadoId;
    private MetodoPago metodoPago;
}