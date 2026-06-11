package com.Gestion.MiBalnearioGestion.Usuarios.Servicio;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.ActualizarPerfilRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarNombreUsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {
    List<UsuarioDTO> buscarTodosUsuarios(String username,
                                         String usernameContiene,
                                         Boolean activos,
                                         Boolean noActivos);
    UsuarioDTO buscarPorIdPublica(UUID idPublica);
    UsuarioDTO actualizarUsuario (UUID idPublica, ActualizarPerfilRequest request);
    void cambiarContrasenia(UUID id, CambiarContraseniaRequest request);
    void darDeBaja (UUID idPublica);
    void cambiarNombreUsuario(UUID usuarioPublicId, CambiarNombreUsuarioDTO request);
}
