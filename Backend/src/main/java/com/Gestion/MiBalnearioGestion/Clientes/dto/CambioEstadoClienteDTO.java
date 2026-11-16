package com.Gestion.MiBalnearioGestion.Clientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambioEstadoClienteDTO(
        @NotNull(message = "Indicá si la cuenta queda activa o dada de baja")
        Boolean activo,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 5, max = 300, message = "El motivo debe tener entre 5 y 300 caracteres")
        String motivo
) {}