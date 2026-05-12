package com.Gestion.MiBalnearioGestion.Usuarios;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {
    UsuarioDTO crearUsuario (UsuarioDTO dtoUsuario);
    List<UsuarioDTO> buscarTodosUsuarios();
    UsuarioDTO buscarPorIdPublica(UUID idPublica);
    UsuarioDTO actualizarUsuario (UUID idPublica, UsuarioDTO dtoUsuario);
    UsuarioDTO buscarPorNombreUsuario(String nombreUsuario);
    void borrarUsuario (UUID idPublica);
}
