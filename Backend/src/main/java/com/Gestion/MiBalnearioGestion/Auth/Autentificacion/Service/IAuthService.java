package com.Gestion.MiBalnearioGestion.Auth.Autentificacion.Service;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthResponse;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;

public interface IAuthService {
    void logout(String authHeader);
    AuthResponse refreshAccessToken(String refreshToken);
    UsuarioDTO register(NewAccountRequest request);
    AuthResponse authenticate(AuthRequest input);
}
