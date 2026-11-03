package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "DTO de solicitud para registrar un pedido a ser entregado en un punto físico del establecimiento (carpa, sombrilla, etc.)")
public class PedidoLugarDTO extends PedidoRequest {

    @Schema(description = "UUID público del recurso físico donde se encuentra el cliente y se entregará el pedido", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    @NotNull(message = "Debe indicar el recurso (carpa o sombrilla)")
    private UUID recursoPublicId;

    @Schema(description = "UUID público del cliente que efectúa la compra", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
    @NotNull(message = "Debe indicar el cliente")
    private UUID clienteId;

    @Schema(description = "UUID público del empleado que toma o registra el pedido (opcional si lo hace el cliente)", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
    private UUID empleadoId;

    @Schema(description = "Método de pago seleccionado para la transacción", implementation = MetodoPago.class)
    private MetodoPago metodoPago;
}