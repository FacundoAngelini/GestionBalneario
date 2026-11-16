package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.RecursoResumenDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CobroComprobanteDTO(
        UUID ticketPublicId,
        UUID reservaPublicId,
        LocalDateTime fecha,
        String clienteNombre,
        Integer clienteDni,
        List<RecursoResumenDTO> recursos,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EModalidad modalidad,
        double subtotal,
        double descuentoPorcentaje,
        double descuentoMonto,
        double total,
        MetodoPago metodoPago,
        String cobradoPor,
        String empleado
) {}