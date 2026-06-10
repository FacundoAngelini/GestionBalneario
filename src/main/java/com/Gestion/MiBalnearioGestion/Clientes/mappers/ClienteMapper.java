package com.Gestion.MiBalnearioGestion.Clientes.mappers;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ClienteMapper implements IMapper<ClienteEntity, ClienteRequest> {

        private final ModelMapper modelMapper;

        public ClienteRequest convertToDTO(ClienteEntity clienteMapeado) {
            return modelMapper.map(clienteMapeado, ClienteRequest.class);
        }

        public ClienteEntity convertToEntity(ClienteRequest cliente_A_DTO, Class<ClienteEntity> clienteEntityClass) {
            return modelMapper.map(cliente_A_DTO, ClienteEntity.class);
        }

        public void updateEntityFromDTO(ClienteRequest dto, ClienteEntity entity) {
        modelMapper.map(dto, entity);
    }
}
