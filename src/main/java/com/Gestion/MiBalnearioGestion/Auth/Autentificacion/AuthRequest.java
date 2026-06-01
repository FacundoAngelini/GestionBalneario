package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "El usuario no puede estar vacío")
        String nombreUsuario,
        @NotBlank(message = "La contraseña no puede estar vacía")
        String contrasenia
) {
}
