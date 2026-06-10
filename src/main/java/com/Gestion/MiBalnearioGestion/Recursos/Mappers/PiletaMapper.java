package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@RequiredArgsConstructor
public class PiletaMapper implements IMapper<PiletaEntity, PiletaDTO> {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.typeMap(PiletaDTO.class, PiletaEntity.class)
                .addMappings(mapper -> mapper.skip(PiletaEntity::setPublicId));
    }

    @Override
    public PiletaEntity convertToEntity(PiletaDTO dto, Class<PiletaEntity> piletaEntityClass){
        return modelMapper.map(dto, PiletaEntity.class);
    }

    @Override
    public PiletaDTO convertToDTO(PiletaEntity entity){
        return modelMapper.map(entity, PiletaDTO.class);
    }

    public void updateEntityFromDTO(PiletaDTO dto, PiletaEntity entity){
        modelMapper.map(dto, entity);
    }


}
