package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetallePedidoRequest {


    @NotNull
    private Integer cantidad;

    @NotNull
    private UUID productoId;


}
