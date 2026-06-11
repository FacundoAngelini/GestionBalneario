package com.Gestion.MiBalnearioGestion.Clientes.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletarPerfilClienteDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "El DNI es obligatorio")
    @Positive(message = "El DNI debe ser un número positivo")
    private Integer dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            message = "El email tiene un formato inválido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números")
    private String telefono;
}