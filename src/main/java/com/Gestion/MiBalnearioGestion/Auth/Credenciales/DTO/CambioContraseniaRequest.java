package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import jakarta.validation.constraints.NotBlank;

public record CambioContraseniaRequest (
    @NotBlank
    String contraseniaActual,
    @NotBlank String nuevaContrasenia
)
{}
