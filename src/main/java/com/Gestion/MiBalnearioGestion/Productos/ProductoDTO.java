package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ECategoriaProdcuto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductoDTO {
    private UUID publicId;

    @NotBlank
    private String nombre;

    @NotNull
    private Double precio;

    @NotNull
    private ECategoriaProdcuto categoria;

    @NotNull
    private Boolean productoDisponible;
}
