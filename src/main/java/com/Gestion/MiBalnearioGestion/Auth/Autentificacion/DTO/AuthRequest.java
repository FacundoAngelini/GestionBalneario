package com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de solicitud utilizado para el inicio de sesión tradicional mediante credenciales de usuario")
public record AuthRequest(

        @Schema(description = "Nombre de usuario o identificador único registrado", example = "mariana.gomez")
        @NotBlank(message = "El usuario no puede estar vacío")
        String nombreUsuario,

        @Schema(description = "Contraseña secreta asociada a la cuenta", example = "Segura2026!")
        @NotBlank(message = "La contraseña no puede estar vacía")
        String contrasenia
) {
}