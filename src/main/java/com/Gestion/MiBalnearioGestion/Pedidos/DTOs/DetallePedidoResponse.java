package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponse {
    private UUID publicId;
    private int cantidad;
    private double precio;
    private ProductoDTO producto;
}
