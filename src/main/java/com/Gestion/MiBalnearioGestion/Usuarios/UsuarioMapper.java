package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper implements IMapper<UsuarioEntity, UsuarioDTO> {

    @Override
    public UsuarioDTO convertToDTO(UsuarioEntity usuarioMapeado) {
        if (usuarioMapeado == null) return null;

        UsuarioDTO dto = new UsuarioDTO();

        // Si este usuario tiene un perfil de cliente asociado, sacamos SU ID público
        if (usuarioMapeado.getCliente() != null) {
            dto.setClienteId(usuarioMapeado.getCliente().getPublicId());
        }

        if (usuarioMapeado.getCredencial() != null) {
            CredencialEntity cred = usuarioMapeado.getCredencial();
            dto.setNombreUsuario(cred.getNombreUsuario());
            // ... (tu lógica de roles se mantiene igual)
        }
        return dto;
    }

    @Override
    public UsuarioEntity convertToEntity(UsuarioDTO dto, Class<UsuarioEntity> usuarioEntityClass) {
        if (dto == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        entity.setPublicId(dto.getClienteId());

        // Nota: Como la Credencial se maneja de forma independiente y segura en el Service,
        // no solemos reconstruir la credencial completa desde un UsuarioDTO básico.

        return entity;
    }
}