package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService  implements IProductoService{

    private final ProductoRepository productoRepository;

    private final ProductoMapper productoMapper;

    public List<ProductoDTO> listarTodos(){
        return productoRepository.findByProductoDisponible(true)
                .stream()
                .map(productoMapper::convertToDTO)
                .toList();
    }

    @Transactional
    public ProductoDTO crear(ProductoDTO dto)
    {
        if(productoRepository.existsByPublicId(dto.getPublicId()))
            throw new EntidadExistenteException("El producto que intenta crear ya existe",dto.toString());

        ProductoEntity producto = productoMapper.convertToEntity(dto,ProductoEntity.class);

        return productoMapper.convertToDTO(productoRepository.save(producto));
    }

    @Transactional
    public void borrar (UUID publicId)
    {
        ProductoEntity buscado = productoRepository.
                findByPublicId(publicId)
                .orElseThrow(()-> new EntidadNoEncontradaException("El producto no se encontró  con id: ", publicId.toString()));

        //baja logica
        buscado.setProductoDisponible(false);
        productoRepository.save(buscado);
    }

    @Transactional
    public ProductoDTO actualziar (UUID publicId, ProductoDTO dto) {
        ProductoEntity producto = productoRepository
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(" El Producto no se encontró : ", dto.toString()));

        productoMapper.updateEntityFromDTO(dto, producto);

        return productoMapper.convertToDTO(productoRepository.save(producto));
    }

    public ProductoDTO buscar (UUID publicId)
    {
        return productoRepository.
                findByPublicId(publicId)
                .map(productoMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("El producto no se encontró con id :" , publicId.toString()));
    }

}
