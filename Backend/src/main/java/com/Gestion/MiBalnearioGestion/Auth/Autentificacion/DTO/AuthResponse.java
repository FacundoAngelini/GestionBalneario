package com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta emitido tras una autenticación exitosa, entregando los tokens criptográficos de acceso y refresco")
public record AuthResponse(

        @Schema(
                description = "Token de acceso (Access Token JWT) firmado por el servidor para autorizar peticiones a endpoints protegidos. Suele tener una vida útil corta.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYW5hLmdvbWV6Iiwicm9sZXMiOlsiUk9MRV9DTElFTlRFIl19..."
        )
        String token,

        @Schema(
                description = "Token de refresco (Refresh Token JWT) utilizado para solicitar nuevos Access Tokens de forma transparente sin reautenticar al usuario.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYW5hLmdvbWV6IiwiZXhwIjoxNzE4NTg5NjAwfQ..."
        )
        String refeshToken
){
}