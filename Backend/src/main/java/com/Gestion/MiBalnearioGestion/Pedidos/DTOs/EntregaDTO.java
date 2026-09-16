package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "DTO que representa la logística de entrega de un pedido, vinculando el estado, el pedido y el empleado asignado")
public class EntregaDTO {

    @Schema(description = "UUID público único de la hoja de entrega", example = "d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a")
    private UUID publicId;

    @Schema(description = "Estado actual de la logística. 'true' si ya fue entregado al cliente, 'false' si está en camino o pendiente.", example = "false")
    @NotNull
    private boolean estadoEntrega;

    @Schema(description = "Información completa y detallada del pedido que se está transportando")
    @NotNull
    private PedidoResponse pedidoDTO;

    @Schema(description = "Datos del empleado (mozo o repartidor) responsable de realizar la entrega física")
    @NotNull
    private EmpleadoDTO empleadoDTO;
}
