package com.Gestion.MiBalnearioGestion.Clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarClienteDTO {
    private String nombre;
    private String apellido;

    @Positive(message = "El DNI debe ser un número positivo")
    private Integer dni;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            message = "El email tiene un formato inválido")
    private String email;

    @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números")
    private String telefono;
}