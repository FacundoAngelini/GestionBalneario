package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de solicitud utilizado para modificar el identificador único de acceso (username) del usuario")
public record CambioNombreUsuarioRequest (

        @Schema(
                description = "Nuevo identificador único para el inicio de sesión. No puede estar vacío ni contener solo espacios.",
                example = "mgomez.2026"
        )
        @NotBlank(message = "El nuevo nombre de usuario es obligatorio")
        String nuevoNombreUsuario
) {}