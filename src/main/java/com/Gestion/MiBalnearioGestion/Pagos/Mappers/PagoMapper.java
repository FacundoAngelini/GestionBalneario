package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.DireccionDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.DireccionEntity;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoMapper implements IMapper<PagoEntity, PagoDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoDTO convertToDTO(PagoEntity entity) {
        return modelMapper.map(entity, PagoDTO.class);
    }

    @Override
    public PagoEntity convertToEntity(PagoDTO dto, Class<PagoEntity> entityClass) {
        return modelMapper.map(dto, PagoEntity.class);
    }

    public void updateEntityFromDTO(PagoDTO dto, PagoEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }
}
