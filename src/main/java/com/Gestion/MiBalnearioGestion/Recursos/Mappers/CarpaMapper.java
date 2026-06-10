package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarpaMapper implements IMapper<CarpaEntity, CarpaDTO> {

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
    public CarpaEntity convertToEntity(CarpaDTO carpaDTO, Class<CarpaEntity> carpaEntityClass){
        return modelMapper.map(carpaDTO, CarpaEntity.class);
    }

    @Override
    public CarpaDTO convertToDTO(CarpaEntity carpaEntity){
        return modelMapper.map(carpaEntity, CarpaDTO.class);
    }

    public void  updateToEntityFromDTO(CarpaDTO carpaDTO, CarpaEntity entity){
        modelMapper.map(carpaDTO, CarpaDTO.class);
    }
}
