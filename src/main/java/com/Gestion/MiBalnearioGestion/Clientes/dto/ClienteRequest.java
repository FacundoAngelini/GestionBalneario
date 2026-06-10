package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "Debe ingresar un nombre válido, entre 2-10 caracteres")
    @Min(2)
    @Max(10)
    private String nombre;
    @NotBlank(message = "Debe ingresar un apellido válido, entre 2-10 caracteres")
    @Min(2)
    @Max(10)
    private String apellido;
    @NotNull
    private int dni;
    @NotBlank
    @Email(regexp="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message="El email ingresado tiene un formato invalido")
    private String email;
    @NotBlank
    private String telefono;
    @NotNull
    private UsuarioDTO usuario;

}
