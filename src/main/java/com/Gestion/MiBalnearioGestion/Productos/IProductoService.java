package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface IProductoService {

    public List<ProductoDTO> listarTodos();

    @Transactional
    public ProductoDTO crear(ProductoDTO dto);

    @Transactional
    public void borrar (UUID publicId);


    @Transactional
    public ProductoDTO actualziar (UUID publicId, ProductoDTO dto);


    public ProductoDTO buscar (UUID publicId);
}
