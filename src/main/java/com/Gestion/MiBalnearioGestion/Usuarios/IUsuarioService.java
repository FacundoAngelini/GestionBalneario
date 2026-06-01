package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {
    List<UsuarioDTO> buscarTodosUsuarios();
    UsuarioDTO buscarPorIdPublica(UUID idPublica);
    UsuarioDTO actualizarUsuario (UUID idPublica, UsuarioDTO dtoUsuario);
    void borrarUsuario (UUID idPublica);
}
