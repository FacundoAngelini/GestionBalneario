package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
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
    private PedidoRequest pedidoDTO;

    @NotNull
    private EmpleadoDTO empleadoDTOw;

    @NotNull
    private PagoPedidoReservaDTO pagoPedidoReserva;
}
