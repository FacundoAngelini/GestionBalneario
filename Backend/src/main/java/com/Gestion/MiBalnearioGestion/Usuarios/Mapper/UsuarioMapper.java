package com.Gestion.MiBalnearioGestion.Usuarios.Mapper;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements IMapper<UsuarioEntity, UsuarioDTO>
{

    @Override
    public UsuarioDTO convertToDTO(UsuarioEntity usuarioMapeado)
    {
        if (usuarioMapeado == null) return null;

        UsuarioDTO dto = new UsuarioDTO();
        if (usuarioMapeado.getCliente() != null)
        {
            dto.setClienteId(usuarioMapeado.getCliente().getPublicId());
        }

        if (usuarioMapeado.getCredencial() != null)
        {
            CredencialEntity cred = usuarioMapeado.getCredencial();
            dto.setNombreUsuario(cred.getNombreUsuario());
        }
        return dto;
    }

    @Override
    public UsuarioEntity convertToEntity(UsuarioDTO dto, Class<UsuarioEntity> usuarioEntityClass) {
        if (dto == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        entity.setPublicId(dto.getClienteId());
        return entity;
    }
}