package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;

import java.time.LocalDateTime;

/** Cobro vigente de una reserva. Si la reserva no está cobrada, el campo "cobro" del DTO es null. */
public record CobroResumenDTO(
        Double monto,
        Double descuento,
        MetodoPago metodoPago,
        LocalDateTime fecha,
        String cobradoPor
) {}