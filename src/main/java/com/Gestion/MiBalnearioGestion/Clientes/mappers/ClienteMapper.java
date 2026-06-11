package com.Gestion.MiBalnearioGestion.Clientes.mappers;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ActualizarClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.CompletarPerfilClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ClienteMapper {

        private final ModelMapper modelMapper;
    @PostConstruct
    public void init() {
        // Entity → ResponseDTO: fecha_alta → fechaAlta
        modelMapper.typeMap(ClienteEntity.class, ClienteResponseDTO.class).addMappings(mapper ->
                mapper.map(ClienteEntity::getFecha_alta, ClienteResponseDTO::setFechaAlta)
        );

        // DTO → Entity: nunca pisamos usuario ni publicId desde un DTO
        modelMapper.typeMap(CompletarPerfilClienteDTO.class, ClienteEntity.class).addMappings(mapper -> {
            mapper.skip(ClienteEntity::setUsuario);
            mapper.skip(ClienteEntity::setPublicId);
        });
    }

    public ClienteResponseDTO toResponseDTO(ClienteEntity entity) {
        return modelMapper.map(entity, ClienteResponseDTO.class);
    }

    public void aplicarCompletarPerfil(CompletarPerfilClienteDTO dto, ClienteEntity entity) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
    }

    // Solo pisa los campos que vienen, ignora los null
    public void aplicarActualizacion(ActualizarClienteDTO dto, ClienteEntity entity) {
        if (dto.getNombre()    != null) entity.setNombre(dto.getNombre());
        if (dto.getApellido()  != null) entity.setApellido(dto.getApellido());
        if (dto.getDni()       != null) entity.setDni(dto.getDni());
        if (dto.getEmail()     != null) entity.setEmail(dto.getEmail());
        if (dto.getTelefono()  != null) entity.setTelefono(dto.getTelefono());
    }
}

