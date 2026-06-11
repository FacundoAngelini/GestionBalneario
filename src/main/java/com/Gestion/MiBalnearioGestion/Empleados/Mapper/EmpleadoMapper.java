package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.ActualizarEmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.DireccionEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper implements IMapper<EmpleadoEntity, EmpleadoDTO> {
    private final ModelMapper modelMapper;

    // Al construir el Bean, le enseñamos cómo resolver los campos planos del usuario
    public EmpleadoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.typeMap(EmpleadoEntity.class, EmpleadoDTO.class).addMappings(mapper -> {
            // Mapea Empleado -> Usuario -> publicId
            mapper.map(src -> src.getUsuario() != null ? src.getUsuario().getPublicId() : null,
                    EmpleadoDTO::setUsuarioPublicId);

            // Mapea Empleado -> Usuario -> Credencial -> nombreUsuario
            mapper.map(src -> (src.getUsuario() != null && src.getUsuario().getCredencial() != null)
                            ? src.getUsuario().getCredencial().getNombreUsuario() : null,
                    EmpleadoDTO::setNombreUsuario);
        });
    }

    public void actualizarEntidadDesdeRequest(ActualizarEmpleadoDTO request, EmpleadoEntity empleado) {

        // 1. Seteamos los datos planos
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setEmail(request.getEmail());
        empleado.setTelefono(request.getTelefono());
        empleado.setSueldo(request.getSueldo());
        empleado.setEstadoEmpleado(request.getEstado());

        // 2. Encapsulamos el uso de ModelMapper acá adentro para la dirección
        if (request.getDireccion() != null) {
            empleado.setDireccion(modelMapper.map(request.getDireccion(), DireccionEntity.class));
        }
    }

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
