package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "El token de refresco no puede estar vacío")
        String refreshToken
) {
}