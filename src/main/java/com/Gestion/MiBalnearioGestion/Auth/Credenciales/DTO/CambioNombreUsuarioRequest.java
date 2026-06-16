package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import jakarta.validation.constraints.NotBlank;

public record CambioNombreUsuarioRequest (
    @NotBlank
    String nuevoNombreUsuario
)
{}
