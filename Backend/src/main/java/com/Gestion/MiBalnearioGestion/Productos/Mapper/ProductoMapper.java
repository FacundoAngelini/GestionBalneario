package com.Gestion.MiBalnearioGestion.Productos.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ProductoEntity;
import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoMapper implements IMapper<ProductoEntity, ProductoDTO> {

    private final ModelMapper modelMapper;

    @Override
    public ProductoDTO convertToDTO(ProductoEntity productoEntity) {
        return modelMapper.map(productoEntity, ProductoDTO.class);
    }

    @Override
    public ProductoEntity convertToEntity(ProductoDTO productoDTO, Class<ProductoEntity> entityClass) {
        return modelMapper.map(productoDTO, entityClass);
    }

    public void updateEntityFromDTO(ProductoDTO dto, ProductoEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }
}
