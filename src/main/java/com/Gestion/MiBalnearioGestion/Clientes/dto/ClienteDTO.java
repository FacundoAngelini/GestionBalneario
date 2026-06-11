package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;

    private UUID publicId;
    @NotNull
    private int dni;
    @NotBlank
    @Email(regexp="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message="El email ingresado tiene un formato invalido")
    private String email;
    @NotBlank
    private String telefono;
    @NotNull
    private LocalDate fecha_alta;
   /* @NotNull
    private UsuarioDTO usuario; no puede estar sino seria dependen cia cirucla,r el usuario debe tener un empleado o cliente, no puede ser lo contrario */

}
