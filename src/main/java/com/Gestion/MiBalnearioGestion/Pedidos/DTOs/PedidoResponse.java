package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
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
    private Long id;
    private UUID publicId;
    private LocalDate fechaPedido;
    private ETipoPedido tipoPedido;
    private List<DetallePedidoResponse> detalles;
    private List<EmpleadoResponseDTO> empleados;
}