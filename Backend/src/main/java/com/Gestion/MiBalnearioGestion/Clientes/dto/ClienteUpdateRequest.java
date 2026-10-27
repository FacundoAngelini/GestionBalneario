package com.Gestion.MiBalnearioGestion.Clientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Datos personales que el cliente puede modificar desde su perfil"
)
public record ClienteUpdateRequest(

        @Schema(description = "Nombre del cliente")
        @NotBlank(message = "Debe ingresar un nombre válido")
        @Size(
                min = 2,
                max = 50,
                message = "El nombre debe tener entre 2 y 50 caracteres"
        )
        String nombre,

        @Schema(description = "Apellido del cliente")
        @NotBlank(message = "Debe ingresar un apellido válido")
        @Size(
                min = 2,
                max = 50,
                message = "El apellido debe tener entre 2 y 50 caracteres"
        )
        String apellido,

        @Schema(description = "Correo electrónico del cliente")
        @NotBlank(message = "El email es obligatorio")
        @Email(
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                message = "El email ingresado tiene un formato inválido"
        )
        String email,

        @Schema(description = "Teléfono del cliente")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^[0-9]+$",
                message = "El teléfono debe contener solo números"
        )
        String telefono
) {
}