package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que contiene la respuesta tras procesar el checkout de una reserva")
public class CheckoutResponseDTO {
    @Schema(description = "UUID público de la reserva generada o procesada", example = "7b4c3d2e-1a0f-4e9b-8c7d-6e5f4a3b2c1d")
    private UUID reservaPublicId;

    @Schema(description = "UUID público del registro de pago asociado", example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c")
    private UUID pagoPublicId;

    @Schema(description = "URL de la pasarela de pago (Mercado Pago) para que el cliente complete la transacción",
            example = "https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=123456789-abcde")
    private String urlMercadoPago;

    @Schema(description = "Monto total facturado en la operación", example = "25500.50")
    private Double montoTotal;

    @Schema(description = "Mensaje descriptivo sobre el estado del checkout", example = "Checkout generado con éxito. Redirigir al usuario para el pago.")
    private String mensaje;
}