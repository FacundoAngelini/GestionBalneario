package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoReservaResponseDTO {
    private UUID publicId;
    private Double monto;
    private EestadoPago estadoPago;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private Double descuento;
}