package com.Gestion.MiBalnearioGestion.Clientes.mappers;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.Imapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class ClienteMapper implements Imapper<ClienteEntity, ClienteDTO> {
        @Autowired
        private ModelMapper modelMApper;

        public ClienteDTO converToDto(ClienteEntity clienteMapeado) {
            return modelMApper.map(clienteMapeado, ClienteDTO.class);
        }

        public ClienteEntity converToEntity(ClienteDTO cliente_A_DTO, Class<ClienteEntity> clienteEntityClass) {
            return modelMApper.map(cliente_A_DTO, ClienteEntity.class);
        }

}
