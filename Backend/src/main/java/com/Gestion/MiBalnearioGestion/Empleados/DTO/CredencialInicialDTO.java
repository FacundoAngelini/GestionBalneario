package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Usuario y contraseña con los que se da de alta a un empleado (las mismas reglas que el registro de clientes). */
public record CredencialInicialDTO(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String nombreUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "La contraseña debe contener una mayúscula, una minúscula, un número y un símbolo (@$!%*?&)")
        String contrasenia
) {}