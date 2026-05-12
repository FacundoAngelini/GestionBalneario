package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.DireccionEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDTO {
    private String nombre;
    private String apellido;
    private int dni;
    private String email;
    private double sueldo;
    private String cuit;
    private EEstadoEmpleado estado;
    @Valid
    @NotNull(message = "La direccion no puede estar vacia")
    private DireccionEntity direccion;
    private UsuarioDTO usuario;

}
