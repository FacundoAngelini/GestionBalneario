package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoResponseDTO {
    private UUID publicId;
    private String nombre;
    private String apellido;
    private int dni;
    private String email;
    private double sueldo;
    private String cuit;
    private EEstadoEmpleado estado;
    private String telefono;
    private DireccionDTO direccion;
    private SectorDTO sector;
    private RolDTO rol;

    private UUID usuarioPublicId; // ← solo el publicId, sin credenciales
}