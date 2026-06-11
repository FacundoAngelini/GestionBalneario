package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ActualizarPerfilRequest {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String telefono;
    @Positive
    private int dni;
}
