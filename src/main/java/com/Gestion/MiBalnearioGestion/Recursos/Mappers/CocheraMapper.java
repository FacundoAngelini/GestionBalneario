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
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(CocheraDTO.class, CocheraEntity.class)
                .addMappings(mapper -> mapper.skip(CocheraEntity::setPublicId));
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
