package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private Double precio;

    @NotNull
    private UUID productoId;


}
