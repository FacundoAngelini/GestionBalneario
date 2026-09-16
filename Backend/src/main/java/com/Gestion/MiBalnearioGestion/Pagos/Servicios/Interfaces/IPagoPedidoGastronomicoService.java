package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;

import java.util.UUID;

public interface IPagoPedidoGastronomicoService {
    TicketDTO procesarPagoPresencial(UUID pedidoPublicId,
                                     UUID empleadoPublicId,
                                     MetodoPago metodo);
}
