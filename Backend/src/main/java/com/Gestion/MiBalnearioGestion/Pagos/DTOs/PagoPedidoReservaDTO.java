package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.EntregaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoLugarDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de solicitud para registrar el pago de un pedido realizado desde un recurso reservado (carpa/sombrilla) junto con su logística de entrega")
public class PagoPedidoReservaDTO extends PagoDTO {

    @Schema(description = "Detalle del pedido y geolocalización del recurso (carpa o sombrilla) desde donde se consumió")
    @NotNull
    private PedidoLugarDTO pedidoLugarDTO;

    @Schema(description = "Información del estado de la distribución logística y del mozo/repartidor asignado para llevar el pedido")
    @NotNull
    private EntregaDTO entregaDTO;
}