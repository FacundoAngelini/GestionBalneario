package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Objeto de solicitud para agregar un producto y su cantidad a un pedido (Línea de detalle)")
public class DetallePedidoRequest {

    @Schema(description = "Cantidad de unidades solicitadas del producto. Debe ser igual o mayor a 1.", example = "3")
    @NotNull
    @Min(1)
    private Integer cantidad;

    @Schema(description = "UUID público del producto que se desea añadir al pedido", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    @NotNull
    private UUID productoId;
}