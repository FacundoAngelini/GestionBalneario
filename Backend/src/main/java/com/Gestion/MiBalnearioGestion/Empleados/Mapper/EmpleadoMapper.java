package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoUpdateDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.DireccionEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpleadoMapper {
    private final ModelMapper modelMapper;

    public EmpleadoResponseDTO convertToResponseDTO(EmpleadoEntity entity) {
        EmpleadoResponseDTO dto = modelMapper.map(entity, EmpleadoResponseDTO.class);
        if (entity.getUsuario() != null) {
            dto.setUsuarioPublicId(entity.getUsuario().getPublicId());
        }
        return dto;
    }

    /** Alta: se arma a mano (sin ModelMapper) para que ningún dato se cuele por coincidencia de nombres. */
    public EmpleadoEntity convertToEntity(EmpleadoDTO dto) {
        return EmpleadoEntity.builder()
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .dni(dto.getDni())
                .email(dto.getEmail().trim())
                .sueldo(dto.getSueldo())
                .cuit(dto.getCuit().trim())
                .telefono(dto.getTelefono().trim())
                .direccion(new DireccionEntity(
                        dto.getDireccion().getCalle().trim(), dto.getDireccion().getNumero(),
                        dto.getDireccion().getCiudad().trim(), dto.getDireccion().getProvincia().trim()))
                .estadoEmpleado(EEstadoEmpleado.ACTIVO)
                .build();
    }

    public void updateEntityFromDTO(EmpleadoUpdateDTO dto, EmpleadoEntity entity) {
        entity.setNombre(dto.getNombre().trim());
        entity.setApellido(dto.getApellido().trim());
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail().trim());
        entity.setSueldo(dto.getSueldo());
        entity.setCuit(dto.getCuit().trim());
        entity.setTelefono(dto.getTelefono().trim());
        entity.setDireccion(new DireccionEntity(
                dto.getDireccion().getCalle().trim(), dto.getDireccion().getNumero(),
                dto.getDireccion().getCiudad().trim(), dto.getDireccion().getProvincia().trim()));
    }
}