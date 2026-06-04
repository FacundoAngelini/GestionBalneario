package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.DireccionDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.DireccionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DireccionMapper implements IMapper<DireccionEntity, DireccionDTO> {

    private final ModelMapper modelMapper;

    public DireccionDTO convertToDTO(DireccionEntity direccionMapeada) {
        return modelMapper.map(direccionMapeada, DireccionDTO.class);
    }

    public DireccionEntity convertToEntity(DireccionDTO direccion_A_DTO, Class<DireccionEntity> direccionEntityClass) {
        return modelMapper.map(direccion_A_DTO, DireccionEntity.class);
    }
}
