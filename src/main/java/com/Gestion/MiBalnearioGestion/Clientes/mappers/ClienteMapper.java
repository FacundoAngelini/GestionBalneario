package com.Gestion.MiBalnearioGestion.Clientes.mappers;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class ClienteMapper implements IMapper<ClienteEntity, ClienteDTO> {
        @Autowired
        private ModelMapper modelMApper;

    public ClienteDTO convertToDTO(ClienteEntity clienteMapeado) {
            return modelMApper.map(clienteMapeado, ClienteDTO.class);
        }

        public ClienteEntity convertToEntity(ClienteDTO cliente_A_DTO, Class<ClienteEntity> clienteEntityClass) {
            return modelMApper.map(cliente_A_DTO, ClienteEntity.class);
        }

}
