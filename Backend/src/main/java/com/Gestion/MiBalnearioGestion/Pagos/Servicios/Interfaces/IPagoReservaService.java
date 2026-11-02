package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;

import java.util.UUID;

public interface IPagoReservaService {
    PagoReservaResponseDTO procesarPagoEfectivoMostrador(ReservaDTO reservaDTO, UUID empleadoPublicId);
}
