package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de solicitud utilizado para renovar el Access Token expirado utilizando un token de refresco válido")
public record RefreshTokenRequest(

        @Schema(
                description = "Token de refresco (Refresh Token) emitido previamente durante el inicio de sesión exitoso",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYW5hLmdvbWV6IiwiZXhwIjoxNzE4NTg5NjAwfQ..."
        )
        @NotBlank(message = "El token de refresco no puede estar vacío")
        String refreshToken
) {
}