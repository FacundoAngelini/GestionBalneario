package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.AuthRequest;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.*;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EmpleadoDTO {
    @NotBlank
    private String nombre;

    @NotNull
    private UUID IDpublico;

    @NotBlank
    private String apellido;

    @NotNull
    private int dni;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private double sueldo;

    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo caracteres numéricos")
    private String cuit;

    @NotNull
    private EEstadoEmpleado estado;

    private String rolSolicitado;

    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo caracteres numéricos")
    @NotNull
    private String telefono;

    @Valid
    @NotNull(message = "La direccion no puede estar vacia")
    private DireccionEntity direccion;

    private UsuarioDTO usuario;// no entity y posible cambio
    private RolEntity rol;
    private SectorEntity sector;

    @Valid
    @NotNull
    private AuthRequest credencial;  //para mandar al usuario ya con la contraseña y nombreUsuario 

}
