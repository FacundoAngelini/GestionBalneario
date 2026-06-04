package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanchaMapper implements IMapper<CanchaEntity, CanchaDTO> {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapper() {
        // 1. Evitamos que si un campo viene null en el DTO, te borre lo que ya habia en la BD
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // 2. Le decimos explicitamente que cuando actualice una CanchaEntity,
        // JAMÁS intente pisar el publicId heredado del Padre.
        modelMapper.typeMap(CanchaDTO.class, CanchaEntity.class)
                .addMappings(mapper -> mapper.skip(CanchaEntity::setPublicId));
    }

    @Override
    public CanchaEntity convertToEntity (CanchaDTO dto, Class<CanchaEntity> canchaEntityClass){
        return modelMapper.map(dto, CanchaEntity.class);
    }

    @Override
    public CanchaDTO convertToDTO  (CanchaEntity entity){
        return modelMapper.map(entity, CanchaDTO.class);
    }

    public void updateToEntity (CanchaDTO dto, CanchaEntity canchaEntity){
        modelMapper.map(dto, canchaEntity);
    }
}
