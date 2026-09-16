package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITicketService {
    TicketDTO ticketDeUnPago(UUID publicId_pago);
    TicketDTO buscarPorPublicId(UUID publicId);
    List<TicketDTO> listarTicketsConFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta, UUID empleadoPublicId);
}
