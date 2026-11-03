package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Estructura de solicitud para registrar de forma rápida un pago presencial directamente en la caja o mostrador")
public record PagoPresencialRequest(

        @Schema(description = "UUID público del pedido que el cliente se dispone a abonar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @NotNull
        UUID pedidoPublicId,

        @Schema(description = "UUID público del cajero o empleado que recibe el dinero físicamente en el establecimiento", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
        @NotNull
        UUID empleadoPublicId,

        @Schema(description = "Método de pago físico o digital utilizado por el cliente en el mostrador (Ej: EFECTIVO, TARJETA_DEBITO, MERCADO_PAGO)", implementation = MetodoPago.class)
        @NotNull
        MetodoPago metodoPago
) {}