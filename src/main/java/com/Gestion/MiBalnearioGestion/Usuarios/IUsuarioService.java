package com.Gestion.MiBalnearioGestion.Usuarios;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {
    public UsuarioDTO crearUsuario (UsuarioDTO dtoUsuario);
    public List<UsuarioDTO> listarUsuarios();
    UsuarioDTO buscarPorIdPublica(UUID idPublica);
    UsuarioDTO actualizarUsuario (UUID idPublica, UsuarioDTO dtoUsuario);
    void BorrarUsuario (UUID idPublica);
}
