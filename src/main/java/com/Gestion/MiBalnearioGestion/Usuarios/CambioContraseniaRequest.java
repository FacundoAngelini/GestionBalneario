package com.Gestion.MiBalnearioGestion.Usuarios;

import jakarta.validation.constraints.NotBlank;

public record CambioContraseniaRequest (
    @NotBlank
    String contraseniaActual,
    @NotBlank String nuevaContrasenia
)
{}
