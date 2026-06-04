package com.Gestion.MiBalnearioGestion.Auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "El token de refresco no puede estar vacío")
        String refreshToken
) {
}