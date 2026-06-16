package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;

import java.util.UUID;

public record PagoPresencialRequest(
        UUID pedidoPublicId,
        UUID empleadoPublicId,
        MetodoPago metodoPago
) {}