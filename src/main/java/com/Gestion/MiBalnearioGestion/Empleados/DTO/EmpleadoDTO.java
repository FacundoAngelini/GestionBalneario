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

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDTO {
    private String nombre;
    private UUID IDpublico;
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
    //METODOS DE CREACION = CRUD
    // METODOS DE BUSQUEDA Y FRILYRADO = ATRIBUTO
    /* PASOS A SEGUIR=
    CREAR CRUD DE COBTROLADOR (CREAR, BUSCAR, ACTUALIZAR Y BOORAR)

    IMPLEMNETAR VAIDATION API

    HACER OPEN API Y DTOS

    LOGIN

    IMPLEMENTAR METODOS DE BUSQUEDA Y FILTRADO EN EL CONTROLADOR


     */

}
