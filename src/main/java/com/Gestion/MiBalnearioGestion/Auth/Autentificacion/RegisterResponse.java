package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UsuarioDTO usuario;
    private String token;
    private String refreshToken;
}