package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private UUID publicId;
    private LocalDate fechaPedido;
    private ETipoPedido tipoPedido;
    private EEstadoPedido estadoPedido;
    private List<DetallePedidoResponse> detalles;
    private List<UUID> empleadosIds;
    private String linkPago;
}