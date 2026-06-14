package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ECategoriaProdcuto;
import jakarta.transaction.Transactional;

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
