package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@Schema(description = "DTO base de solicitud para la creación de cualquier tipo de pedido en el sistema")
public class PedidoRequest {

    @Schema(description = "Tipo o modalidad del pedido (Ej: EXPRESS, MESA, LUGAR, TAKE_AWAY, DELIVERY)", implementation = ETipoPedido.class)
    @NotNull
    private ETipoPedido tipoPedido;

    @Schema(description = "Listado de productos seleccionados junto con sus respectivas cantidades (Líneas de comanda)")
    @NotNull
    @NotEmpty(message = "El pedido debe contener al menos un producto")
    private List<DetallePedidoRequest> pedidos;
}