package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta que representa el estado consolidado y el detalle completo de un pedido procesado")
public class PedidoResponse {

    @Schema(description = "UUID público único del pedido", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID publicId;

    @Schema(description = "Fecha en la que se registró y dio de alta el pedido", example = "2026-06-16")
    private LocalDate fechaPedido;

    @Schema(description = "Modalidad o tipo de pedido registrado", implementation = ETipoPedido.class)
    private ETipoPedido tipoPedido;

    @Schema(description = "Estado actual del ciclo de vida del pedido (Ej: PENDIENTE, PREPARANDO, ENTREGADO, CANCELADO)", implementation = EEstadoPedido.class)
    private EEstadoPedido estadoPedido;

    @Schema(description = "Listado detallado de los productos adquiridos, cantidades y precios históricos facturados")
    private List<DetallePedidoResponse> detalles;

    @Schema(description = "Lista de UUIDs públicos de los empleados involucrados en el pedido (Ej: mozo que tomó el pedido, repartidor que lo entregó)", example = "[\"3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d\"]")
    private List<UUID> empleadosIds;

    @Schema(description = "URL de la pasarela de pagos externa generada para que el cliente pueda abonar el pedido en línea", example = "https://link.mercadopago.com.ar/checkout/v1/payment/123456")
    private String linkPago;
}