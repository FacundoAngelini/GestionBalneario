package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoUpdateDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class EmpleadoMapper {
    private final ModelMapper modelMapper;

    // Entity → ResponseDTO (para respuestas)
    public EmpleadoResponseDTO convertToResponseDTO(EmpleadoEntity entity) {
        EmpleadoResponseDTO dto = modelMapper.map(entity, EmpleadoResponseDTO.class);
        if (entity.getUsuario() != null) {
            dto.setUsuarioPublicId(entity.getUsuario().getPublicId());
        }
        return dto;
    }

    // DTO → Entity (para crear)
    public EmpleadoEntity convertToEntity(EmpleadoDTO dto) {
        return modelMapper.map(dto, EmpleadoEntity.class);
        // credencial no se mapea porque EmpleadoEntity no tiene ese campo
    }

    // Para actualizar
    public void updateEntityFromDTO(EmpleadoUpdateDTO dto, EmpleadoEntity entity){
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setSueldo(dto.getSueldo());
        entity.setCuit(dto.getCuit());
        entity.setTelefono(dto.getTelefono());
        entity.setEstadoEmpleado(dto.getEstado());

        if(dto.getDireccion()!=null){
            entity.getDireccion().setCalle(dto.getDireccion().getCalle());
            entity.getDireccion().setNumero(dto.getDireccion().getNumero());
            entity.getDireccion().setCiudad(dto.getDireccion().getCiudad());
            entity.getDireccion().setProvincia(dto.getDireccion().getProvincia());
        }
    }
}
