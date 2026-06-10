package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapperDual;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ClienteMapper implements IMapperDual<ClienteEntity, ClienteRequest, ClienteResponse> {

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

        @Override
        public ClienteResponse convertToResponseDTO(ClienteEntity entity) {
                return modelMapper.map(entity, ClienteResponse.class);
        }
}
