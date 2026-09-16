package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.*;
import com.Gestion.MiBalnearioGestion.Sector.DTO.SectorDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO  para el registro, actualización y consulta detallada de la información del personal del establecimiento")
public class EmpleadoDTO {

    @Schema(description = "UUID público único del empleado en el sistema", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
    private UUID IDpublico;

    @Schema(description = "Nombres del empleado", example = "Juan Carlos")
    @NotBlank
    private String nombre;

    @Schema(description = "Apellidos del empleado", example = "Pérez")
    @NotBlank
    private String apellido;

    @Schema(description = "Documento Nacional de Identidad", example = "34567890")
    @NotNull
    private Integer dni;

    @Schema(description = "Correo electrónico institucional o de contacto", example = "juan.perez@establecimiento.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Sueldo o remuneración base asignada", example = "450000.00")
    @NotNull
    private Double sueldo;

    @Schema(description = "Clave Única de Identificación Tributaria (Solo números)", example = "20345678909")
    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo caracteres numéricos")

    private String cuit;

    @Schema(description = "Estado operativo actual del empleado en la empresa (Ej: ACTIVO, LICENCIA, INACTIVO)", implementation = EEstadoEmpleado.class)
    @NotNull
    private EEstadoEmpleado estado;

    @Schema(description = "Rol o puesto solicitado durante el proceso de postulación/alta rápida", example = "CAJERO", nullable = true)
    private String rolSolicitado;

    @Schema(description = "Número telefónico de contacto (Solo números)", example = "2235123456")
    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo caracteres numéricos")
    @NotBlank
    private String telefono;

    @Schema(description = "Datos de localización domiciliaria del empleado")
    @Valid
    @NotNull(message = "La direccion no puede estar vacia")
    private DireccionDTO direccion;

    @Schema(description = "Datos de la cuenta de usuario vinculada para accesos generales", nullable = true)
    private UsuarioDTO usuario;

    @Schema(description = "Rol de seguridad asignado en el sistema (Spring Security)", nullable = true)
    private RolDTO rol;

    @Schema(description = "Sector físico u operativo asignado dentro del complejo (Ej: Cocina, Barra, Carpas)", nullable = true)
    private SectorDTO sector;

    @Schema(description = "Credenciales iniciales de acceso (Usuario y contraseña) requeridas para dar de alta su cuenta en el sistema de manera simultánea")
    @Valid
    @NotNull
    private AuthRequest credencial;
}