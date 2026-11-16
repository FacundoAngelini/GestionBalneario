package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnularCobroDTO(
        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 5, max = 300, message = "El motivo debe tener entre 5 y 300 caracteres")
        String motivo
) {}