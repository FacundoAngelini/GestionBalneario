package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EAccionReserva;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoReservaDTO(
        UUID publicId,
        EAccionReserva accion,
        String usuario,
        String empleado,
        LocalDateTime fecha,
        String motivo,
        String detalle
) {}