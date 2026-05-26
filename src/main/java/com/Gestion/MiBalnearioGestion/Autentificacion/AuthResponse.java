package com.Gestion.MiBalnearioGestion.Autentificacion;

import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UsuarioDTO usuario;
}
