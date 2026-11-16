package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Common.Validacion.TelefonoValido;
import jakarta.validation.constraints.*;

/** Datos que el personal puede modificar de un cliente. */
public record ClienteEdicionDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre admite hasta 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50, message = "El apellido admite hasta 50 caracteres")
        String apellido,

        @NotNull(message = "El DNI es obligatorio")
        @Min(value = 1_000_000, message = "El DNI no es válido")
        @Max(value = 99_999_999, message = "El DNI no es válido")
        Integer dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "El email tiene un formato inválido")
        @Size(max = 75, message = "El email no puede contener mas de 75 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @TelefonoValido
        String telefono
) {}