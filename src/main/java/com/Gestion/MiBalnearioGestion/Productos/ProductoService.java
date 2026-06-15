package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService  implements IProductoService{

    private final ProductoRepository productoRepository;

    private final ProductoMapper productoMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ProductoDTO> listarTodos(String nombre,
                                         ECategoriaProdcuto categoria,
                                         Boolean disponible) {

        PredicateSpecification<ProductoEntity> spec =
                PredicateSpecification.allOf(
                        ProductoSpecification.categoriaIgual(categoria),
                        ProductoSpecification.nombreContiene(nombre),
                        ProductoSpecification.disponibleIgual(disponible)
                );
        return productoRepository.findAll(spec)
                .stream()
                .map(productoMapper::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductoDTO> listarDisponibles (){ //este metodo es para que los clientes sepan los productos que pueden pedir y esta disponibles
        return productoRepository.findByProductoDisponibleTrue()
                .stream()
                .map(productoMapper::convertToDTO)
                .toList();
    }

    @Transactional
    @Override
    public ProductoDTO crear(ProductoDTO dto)
    {
        if(productoRepository.findByNombre(dto.getNombre()).isPresent())
        {
            throw new EntidadExistenteException("ya existe un producto con este nombre", "ProductoEntity");
        }

        ProductoEntity producto = productoMapper.convertToEntity(dto,ProductoEntity.class);

        return productoMapper.convertToDTO(productoRepository.save(producto));
    }

    @Transactional
    @Override
    public void borrar (UUID publicId)
    {
        ProductoEntity buscado = productoRepository.
                findByPublicId(publicId)
                .orElseThrow(()-> new EntidadNoEncontradaException("El producto no se encontró  con id: ", publicId.toString()));

        if (!buscado.getProductoDisponible()) {
            throw new IllegalStateException("El producto ya se encuentra dado de baja.");
        }

        buscado.setProductoDisponible(false);
        productoRepository.save(buscado);
    }

    @Transactional
    @Override
    public ProductoDTO actualziar (UUID publicId, ProductoDTO dto) {
        ProductoEntity producto = productoRepository
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(" El Producto no se encontró : ", dto.toString()));

        Long idOriginal = producto.getId();
        UUID publicIdOriginal = producto.getPublicId();

        productoMapper.updateEntityFromDTO(dto, producto);

        producto.setId(idOriginal);
        producto.setPublicId(publicIdOriginal);

        return productoMapper.convertToDTO(productoRepository.save(producto));
    }
    @Transactional(readOnly = true)
    @Override
    public ProductoDTO buscar (UUID publicId)
    {
        return productoRepository.
                findByPublicId(publicId)
                .map(productoMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("El producto no se encontró con id :" , publicId.toString()));
    }

    @Transactional
    @Override
    public void reactivar(UUID publicId) {
        ProductoEntity buscado = productoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("El producto no se encontró con id: ", publicId.toString()));
        if (buscado.getProductoDisponible()) {
            throw new IllegalStateException("El producto ya se encuentra activo y disponible.");
        }
        buscado.setProductoDisponible(true);
        productoRepository.save(buscado);
    }
}
