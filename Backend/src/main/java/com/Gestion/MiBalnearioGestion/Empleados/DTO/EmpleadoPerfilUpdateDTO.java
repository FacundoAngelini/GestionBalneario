package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpleadoPerfilUpdateDTO(
        @NotBlank @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "El email tiene un formato inválido")
        String email,
        @NotBlank @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números") @Size(min = 9, max = 15)
        String telefono
) {}