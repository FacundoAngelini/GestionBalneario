package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;
@Builder
@Getter
@Schema(description = "DTO de respuesta unificado para el historial de transacciones, aplicable a pedidos, mesas o reservas de alquiler")
public class PagoResponseDTO {

    @Schema(description = "UUID público único del comprobante de pago", example = "5f4e3d2c-b1a0-9e8d-7c6b-5a4f3e2d1c0b")
    private UUID publicId;

    @Schema(description = "Monto neto cobrado en la transacción", example = "12500.00")
    private double monto;

    @Schema(description = "Estado actual de la transacción financiera", implementation = EestadoPago.class)
    private EestadoPago estadoPago;

    @Schema(description = "Fecha en la que se procesó el cobro", example = "2026-06-16")
    private LocalDate fechaPago;

    @Schema(description = "Medio electrónico o físico utilizado para abonar", implementation = MetodoPago.class)
    private MetodoPago metodoPago;

    @Schema(description = "Porcentaje de descuento que fue aplicado al total del ticket", example = "0.0")
    private double descuento;

    @Schema(description = "UUID público de la reserva asociada. [CONDICIONAL]: Presente únicamente si tipoPago es 'RESERVA'.", example = "7a8b9c0d-1e2f-3a4b-5c6d-e5f6d7c8b9a0", nullable = true)
    private UUID reservaPublicId;

    @Schema(description = "UUID público del pedido o comanda asociada. [CONDICIONAL]: Presente únicamente si tipoPago es 'PEDIDO_MESA' o 'PEDIDO_LUGAR'.", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", nullable = true)
    private UUID pedidoPublicId;

    @Schema(description = "Discriminador que define la naturaleza del origen del pago", example = "PEDIDO_MESA", allowableValues = {"RESERVA", "PEDIDO_MESA", "PEDIDO_LUGAR"})
    private String tipoPago;
}