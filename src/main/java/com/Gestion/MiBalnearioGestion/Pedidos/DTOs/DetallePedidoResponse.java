package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponse {
    private UUID publicId;
    private int cantidad;
    private double precio;
    private List<UUID> IDSproductos;
}
