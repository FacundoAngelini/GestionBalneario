package com.Gestion.MiBalnearioGestion.Sector.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Sector.Entity.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.DTO.SectorDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectorMapper implements IMapper<SectorEntity, SectorDTO> {

    private final ModelMapper modelMapper;

    @Override
    public SectorDTO convertToDTO(SectorEntity entity) {
        return modelMapper.map(entity, SectorDTO.class);
    }

    @Override
    public SectorEntity convertToEntity(SectorDTO dto, Class<SectorEntity> clase) {
        return modelMapper.map(dto, SectorEntity.class);
    }

    public void updateEntityFromDTO(SectorDTO dto, SectorEntity entity) {
        modelMapper.typeMap(SectorDTO.class, SectorEntity.class)
                .addMappings(mapper -> mapper.skip(SectorEntity::setId));

        modelMapper.map(dto, entity);
    }
}