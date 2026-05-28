package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Autentificacion.Registrable;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO implements Registrable {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    private UUID publicId;
    @NotBlank
    private int dni;
    @NotBlank
    @Email(regexp="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message="El email ingresado tiene un formato invalido")
    private String email;
    @NotBlank
    private String telefono;
    @NotBlank
    private UsuarioDTO usuario;


    @Override
    public UsuarioDTO getUser() {
        return this.usuario;
    }

    @Override
    public UUID getPublicId() {
        return this.publicId;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
