package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper implements IMapper<UsuarioEntity, UsuarioDTO> {

    private final ModelMapper modelMApper;

    public UsuarioDTO convertToDTO(UsuarioEntity usuarioMapeado) {
        return modelMApper.map(usuarioMapeado, UsuarioDTO.class);
    }

    public UsuarioEntity convertToEntity(UsuarioDTO usuario_A_DTO, Class<UsuarioEntity> usuarioEntityClass)
    {return modelMApper.map(usuario_A_DTO, UsuarioEntity.class);
    }



}
