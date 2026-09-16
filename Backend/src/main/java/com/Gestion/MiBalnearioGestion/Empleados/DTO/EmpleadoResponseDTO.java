package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Sector.DTO.SectorDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta optimizado que expone la ficha pública, estado operativo y asignaciones de un empleado")
public class EmpleadoResponseDTO {

    @Schema(description = "UUID público único del empleado en el sistema", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
    private UUID publicId;

    @Schema(description = "Nombres del empleado", example = "Juan Carlos")
    private String nombre;

    @Schema(description = "Apellidos del empleado", example = "Pérez")
    private String apellido;

    @Schema(description = "Documento Nacional de Identidad", example = "34567890")
    private Integer dni;

    @Schema(description = "Correo electrónico de contacto", example = "juan.perez@establecimiento.com")
    private String email;

    @Schema(description = "Sueldo o remuneración base asignada", example = "450000.00")
    private Double sueldo;

    @Schema(description = "Clave Única de Identificación Tributaria", example = "20345678909")
    private String cuit;

    @Schema(description = "Estado operativo y de disponibilidad actual del empleado", implementation = EEstadoEmpleado.class)
    private EEstadoEmpleado estadoEmpleado;

    @Schema(description = "Número telefónico de contacto", example = "2235123456")
    private String telefono;

    @Schema(description = "Datos completos de la localización domiciliaria del empleado")
    private DireccionDTO direccion;

    @Schema(description = "Información del sector físico u operativo asignado (Ej: Cocina, Barra, Carpas)")
    private SectorDTO sector;

    @Schema(description = "Información del rol de seguridad y permisos asignados en el sistema")
    private RolDTO rol;

    @Schema(description = "UUID público de la cuenta de usuario vinculada al empleado para auditoría o gestión de accesos", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", nullable = true)
    private UUID usuarioPublicId;
}