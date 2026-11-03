package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.CobroComprobanteDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ReservaPresencialDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ReservaPresencialResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;

import java.util.UUID;

public interface IReservaCobroServicio {
    CobroComprobanteDTO registrarCobro(UUID reservaPublicId, MetodoPago metodo);
    CobroComprobanteDTO obtenerComprobante(UUID reservaPublicId);
    ReservaDTO anularCobro(UUID reservaPublicId, String motivo);
    ReservaPresencialResponseDTO crearPresencial(ReservaPresencialDTO dto);
}