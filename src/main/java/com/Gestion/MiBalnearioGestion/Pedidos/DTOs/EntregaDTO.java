package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EntregaDTO {


    private UUID publicId;

    @NotNull
    private boolean estadoEntrega;

    @NotNull
    private PedidoResponse pedidoDTO;

    @NotNull
    private EmpleadoDTO empleadoDTO;

}
