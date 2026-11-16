package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Una reserva vista desde la ficha del cliente. */
public record ReservaResumenDTO(
        UUID publicId, LocalDate fechaInicio, LocalDate fechaFin, EModalidad modalidad,
        EReservaEstado estadoReserva, double montoTotal, List<String> recursos) {}