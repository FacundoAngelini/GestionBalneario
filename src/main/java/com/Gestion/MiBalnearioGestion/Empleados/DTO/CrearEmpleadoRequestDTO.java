package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearEmpleadoRequestDTO {
    @Valid
    @NotNull
    private EmpleadoDTO empleado;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasenia;

    @NotBlank(message = "Debe especificar un rol inicial")
    private String rolSolicitado;
}