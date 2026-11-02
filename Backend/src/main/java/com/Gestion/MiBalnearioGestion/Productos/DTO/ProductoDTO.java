package com.Gestion.MiBalnearioGestion.Productos.DTO;

import com.Gestion.MiBalnearioGestion.Productos.Entity.ECategoriaProdcuto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "DTO que representa un producto comercializable dentro del establecimiento")
public class ProductoDTO {

    @Schema(description = "UUID público único del producto", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
    private UUID publicId;

    @Schema(description = "Nombre comercial o denominación del producto", example = "Licuado de Frutilla con Leche")
    @NotBlank
    private String nombre;

    @Schema(description = "Precio unitario de venta al público", example = "3500.00")
    @NotNull
    private Double precio;

    @Schema(description = "Categoría a la que pertenece el producto", implementation = ECategoriaProdcuto.class)
    @NotNull
    private ECategoriaProdcuto categoria;

    @Schema(description = "Indica si el producto cuenta con stock o se encuentra habilitado para la venta inmediata", example = "true")
    @NotNull
    private Boolean productoDisponible;
}