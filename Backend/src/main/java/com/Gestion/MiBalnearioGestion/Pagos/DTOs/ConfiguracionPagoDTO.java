package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import jakarta.validation.constraints.NotNull;

public record ConfiguracionPagoDTO(@NotNull Double descuentoEfectivo) {}