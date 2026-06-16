package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponseDTO {
    private UUID reservaPublicId;
    private UUID pagoPublicId;
    private String urlMercadoPago;
    private Double montoTotal;
    private String mensaje;
}