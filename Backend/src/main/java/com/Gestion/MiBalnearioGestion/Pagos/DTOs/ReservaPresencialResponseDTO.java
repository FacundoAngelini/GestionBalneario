package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;

/** comprobante es null si la reserva se creó sin cobrar. */
public record ReservaPresencialResponseDTO(ReservaDTO reserva, CobroComprobanteDTO comprobante) {}