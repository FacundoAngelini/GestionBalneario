package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class PagoResponseDTO {
    private UUID publicId;
    private double monto;
    private EestadoPago estadoPago;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private double descuento;

    // Presente solo si es reserva
    private UUID reservaPublicId;

    // Presente solo si es pedido
    private UUID pedidoPublicId;

    // Para saber qué tipo de pago es
    private String tipoPago;
}