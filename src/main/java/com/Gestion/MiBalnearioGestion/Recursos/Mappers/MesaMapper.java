package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MesaMapper implements IMapper<MesaEntity, MesaDTO> {
    private final ModelMapper modelMapper;
    @PostConstruct
    public void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(MesaDTO.class, MesaEntity.class)
                .addMappings(mapper -> mapper.skip(MesaEntity::setPublicId));
    }

    @Override
    public MesaEntity convertToEntity (MesaDTO dto, Class<MesaEntity> entityClass){
        return modelMapper.map(dto, MesaEntity.class);
    }

    @Override
    public MesaDTO convertToDTO (MesaEntity entity){
        return modelMapper.map(entity, MesaDTO.class);
    }

    public void updateToEntity(MesaDTO dto, MesaEntity entity){
        modelMapper.map(dto, entity);
    }
}
