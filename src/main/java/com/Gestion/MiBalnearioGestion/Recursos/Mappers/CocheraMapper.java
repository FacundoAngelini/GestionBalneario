package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CocheraMapper implements IMapper <CocheraEntity, CocheraDTO> {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapper() {
        // 1. Evitamos que si un campo viene null en el DTO, te borre lo que ya habia en la BD
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // 2. Le decimos explicitamente que cuando actualice una CanchaEntity,
        // JAMÁS intente pisar el publicId heredado del Padre.
        modelMapper.typeMap(CarpaDTO.class, CarpaEntity.class)
                .addMappings(mapper -> mapper.skip(CarpaEntity::setPublicId));
    }

    @Override
    public CocheraEntity convertToEntity(CocheraDTO dto, Class<CocheraEntity> clazz) {
        return modelMapper.map(dto, CocheraEntity.class);
    }

    @Override
    public CocheraDTO convertToDTO(CocheraEntity entity) {
        return modelMapper.map(entity, CocheraDTO.class);
    }

    public void updateToEntityFromDTO(CocheraDTO dto, CocheraEntity entity) {
        modelMapper.map(dto, entity);
    }

}
