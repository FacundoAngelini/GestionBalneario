package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.AuthRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "Debe ingresar un nombre válido")
    @Size(min = 2, max = 50)
    private String nombre;

    @NotBlank(message = "Debe ingresar un apellido válido")
    @Size(min = 2, max = 50)
    private String apellido;

    @NotNull
    private int dni;

    @NotBlank
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            message = "El email ingresado tiene un formato inválido")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números")
    private String telefono;

    @Valid
    @NotNull(message = "Las credenciales no pueden estar vacías")
    private AuthRequest credencial; // ← reemplaza UsuarioDTO
}
