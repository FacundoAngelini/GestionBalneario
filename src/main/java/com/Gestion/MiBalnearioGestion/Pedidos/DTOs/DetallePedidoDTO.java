package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetallePedidoDTO {

    @NotNull
    private UUID publicId;

    @NotNull
    private int cantidad;

    @NotNull
    private double precio;

    //@Min quiza se puede implementar para no tener pedidos vacios averiguar como
    @NotNull
    private List<ProductoDTO> productos = new ArrayList<>();

    @NotNull
    private PedidoDTO pedidoDTO;
}
