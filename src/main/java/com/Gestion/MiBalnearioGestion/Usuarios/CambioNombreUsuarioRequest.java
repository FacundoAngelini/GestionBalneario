package com.Gestion.MiBalnearioGestion.Usuarios;

import jakarta.validation.constraints.NotBlank;

public record CambioNombreUsuarioRequest (
    @NotBlank
    String nuevoNombreUsuario
)
{}
