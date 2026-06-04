package com.Gestion.MiBalnearioGestion.Clientes.mappers;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ClienteMapper implements IMapper<ClienteEntity, ClienteDTO> {

        private final ModelMapper modelMapper;

        public ClienteDTO convertToDTO(ClienteEntity clienteMapeado) {
            return modelMapper.map(clienteMapeado, ClienteDTO.class);
        }

        public ClienteEntity convertToEntity(ClienteDTO cliente_A_DTO, Class<ClienteEntity> clienteEntityClass) {
            return modelMapper.map(cliente_A_DTO, ClienteEntity.class);
        }

        public void updateEntityFromDTO(ClienteDTO dto, ClienteEntity entity) {
        modelMapper.map(dto, entity);
    }
}
