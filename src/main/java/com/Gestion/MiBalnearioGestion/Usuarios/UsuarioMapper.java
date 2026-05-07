package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.Imapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements Imapper<UsuarioEntity, UsuarioDTO> {
    @Autowired
    private ModelMapper modelMApper;

    public UsuarioDTO converToDto(UsuarioEntity usuarioMapeado) {
        return modelMApper.map(usuarioMapeado, UsuarioDTO.class);
    }

    public UsuarioEntity converToEntity(UsuarioDTO usuario_A_DTO, Class<UsuarioEntity> usuarioEntityClass)
    {return modelMApper.map(usuario_A_DTO, UsuarioEntity.class);
    }
}
