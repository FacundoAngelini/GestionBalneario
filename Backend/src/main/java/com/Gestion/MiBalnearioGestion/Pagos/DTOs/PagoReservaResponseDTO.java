package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta optimizado que confirma el procesamiento del pago de una reserva de alquiler")
public class PagoReservaResponseDTO {

    @Schema(description = "UUID público único del comprobante de pago", example = "5f4e3d2c-b1a0-9e8d-7c6b-5a4f3e2d1c0b")
    private UUID publicId;

    @Schema(description = "Monto neto cobrado en la transacción", example = "45000.00")
    private Double monto;

    @Schema(description = "Estado actual de la transacción financiera", implementation = EestadoPago.class)
    private EestadoPago estadoPago;

    @Schema(description = "Fecha en la que impactó el pago en el sistema", example = "2026-06-16")
    private LocalDate fechaPago;

    @Schema(description = "Vía o canal transaccional utilizado por el cliente", implementation = MetodoPago.class)
    private MetodoPago metodoPago;

    @Schema(description = "Porcentaje de bonificación o descuento aplicado al total", example = "15.0")
    private Double descuento;

    @Schema(description = "UUID público de la reserva de recurso asociada a este pago", example = "7a8b9c0d-1e2f-3a4b-5c6d-e5f6d7c8b9a0")
    private UUID reservaPublicId;
}