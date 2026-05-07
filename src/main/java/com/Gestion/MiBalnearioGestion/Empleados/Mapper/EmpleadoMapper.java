package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper implements IMapper<EmpleadoEntity, EmpleadoDTO> {
    @Autowired
    private ModelMapper modelMapper;

    public EmpleadoDTO convertToDTO(EmpleadoEntity empleadoMapeado) {
        return modelMapper.map(empleadoMapeado, EmpleadoDTO.class);
    }

    public EmpleadoEntity convertToEntity(EmpleadoDTO empleado_A_DTO, Class<EmpleadoEntity> empleadoEntityClass) {
        return modelMapper.map(empleado_A_DTO, EmpleadoEntity.class);
    }
}
