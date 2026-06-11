package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ActualizarEmpleadoDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String telefono;

    @NotNull
    private double sueldo;

    @NotNull
    private EEstadoEmpleado estado;

    @NotNull
    private Long idSector;

    @NotNull
    private Long idRol;

    @Valid
    @NotNull
    private DireccionDTO direccion;
}
