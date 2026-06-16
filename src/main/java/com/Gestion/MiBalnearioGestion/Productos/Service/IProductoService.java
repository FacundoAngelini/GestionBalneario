package com.Gestion.MiBalnearioGestion.Productos.Service;

import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ECategoriaProdcuto;

import java.util.List;
import java.util.UUID;

public interface IProductoService {

    ProductoDTO crear(ProductoDTO dto);

    void borrar (UUID publicId);

    ProductoDTO actualziar (UUID publicId, ProductoDTO dto);

    ProductoDTO buscar (UUID publicId);

    void reactivar(UUID publicId);

    List<ProductoDTO> listarDisponibles ();

    List<ProductoDTO> listarTodos(String nombre,
                                  ECategoriaProdcuto categoria,
                                  Boolean disponible);
}
