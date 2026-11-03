package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO que representa la información consolidada de una transacción financiera o pago de un ticket")
public class PagoDTO {

    @Schema(description = "UUID público único del registro de pago", example = "5f4e3d2c-b1a0-9e8d-7c6b-5a4f3e2d1c0b")
    private UUID publicId;

    @Schema(description = "Monto neto abonado en la transacción. Debe ser estrictamente mayor a 0.", example = "14500.50")
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El monto mínimo es 0.01")
    private Double monto;

    @Schema(description = "Estado actual de la transacción financiera (Ej: APROBADO, RECHAZADO, PENDIENTE)", implementation = EestadoPago.class)
    @NotNull(message = "El estado de pago no puede ser nulo")
    private EestadoPago eestadoPago;

    @Schema(description = "Fecha en la que se hizo efectiva la transacción (No puede ser una fecha futura)", example = "2026-06-16")
    @NotNull(message = "La fecha de pago no puede ser nula")
    @PastOrPresent(message = "La fecha de pago no puede ser futura")
    private LocalDate fechaPago;

    @Schema(description = "Canal o método de pago seleccionado por el cliente (Ej: EFECTIVO, MERCADO_PAGO, TARJETA_DEBITO, TARJETA_CREDITO)", implementation = MetodoPago.class)
    @NotNull(message = "El método de pago no puede ser nulo")
    private MetodoPago metodoPago;

    @Schema(description = "Porcentaje de descuento aplicado al total del cobro (Rango de 0.0 a 100.0)", example = "10.0", defaultValue = "0.0")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    private Double descuento;

    @Schema(description = "Detalle del comprobante comercial (Ticket) al cual se le imputa este pago")
    @NotNull(message = "El ticket no puede ser nulo")
    private TicketDTO ticketDTO;
}