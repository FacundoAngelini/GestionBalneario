package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
@RequiredArgsConstructor
public class SombrillaMapper implements IMapper<SombrillaEntity, SombrillaDTO> {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(SombrillaDTO.class, SombrillaEntity.class)
                .addMappings(mapper -> mapper.skip(SombrillaEntity::setPublicId));
    }

    @Override
    public SombrillaEntity convertToEntity(SombrillaDTO dto, Class<SombrillaEntity> sombrillaEntityClass){
        return modelMapper.map(dto, SombrillaEntity.class);
    }

    @Override
    public SombrillaDTO convertToDTO(SombrillaEntity entity){
        return modelMapper.map(entity, SombrillaDTO.class);
    }

    public void updateEntityFromDTO(SombrillaDTO dto, SombrillaEntity entity){
        modelMapper.map(dto, entity);
    }
}
