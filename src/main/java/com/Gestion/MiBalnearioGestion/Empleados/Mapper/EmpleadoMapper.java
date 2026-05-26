package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpleadoMapper implements IMapper<EmpleadoEntity, EmpleadoDTO> {
    private ModelMapper modelMapper;

    @Override
    public EmpleadoDTO convertToDTO(EmpleadoEntity empleadoMapeado) {
        return modelMapper.map(empleadoMapeado, EmpleadoDTO.class);
    }

    @Override //vienen de la interfazz y estan sobreescritos
    public EmpleadoEntity convertToEntity(EmpleadoDTO empleado_A_DTO, Class<EmpleadoEntity> empleadoEntityClass) {
        return modelMapper.map(empleado_A_DTO, EmpleadoEntity.class);
    }

    // no lleva override ya que no esta en la interfaz imapper
    public void updateEntityFromDTO(EmpleadoDTO dto, EmpleadoEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }
}
