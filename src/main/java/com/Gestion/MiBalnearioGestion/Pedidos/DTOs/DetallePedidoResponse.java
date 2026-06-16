package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta que detalla una línea específica de un pedido procesado")
public class DetallePedidoResponse {

    @Schema(description = "UUID público único de esta línea de detalle del pedido", example = "c1b2a3f4-e5d6-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID publicId;

    @Schema(description = "Cantidad de unidades adquiridas de este producto", example = "3")
    private int cantidad;

    @Schema(description = "Precio unitario cobrado por el producto al momento de realizar el pedido", example = "3500.00")
    private double precio;

    @Schema(description = "Información comercial completa del producto asociado a este detalle")
    private ProductoDTO producto;
}
