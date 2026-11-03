package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambioEstadoEmpleadoDTO(
        @NotNull(message = "El estado es obligatorio") EEstadoEmpleado estado,
        @Size(max = 300, message = "El motivo admite hasta 300 caracteres") String motivo
) {}