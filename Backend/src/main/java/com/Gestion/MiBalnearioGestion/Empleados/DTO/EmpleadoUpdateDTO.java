package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
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
@Schema(description = "DTO de solicitud utilizado para actualizar la información de la ficha de un empleado, sus condiciones laborales o sus asignaciones operativas")
public class EmpleadoUpdateDTO {

    @Schema(description = "Nombres del empleado a actualizar", example = "Juan Carlos")
    @NotBlank
    private String nombre;

    @Schema(description = "Apellidos del empleado a actualizar", example = "Pérez")
    @NotBlank
    private String apellido;

    @Schema(description = "Documento Nacional de Identidad", example = "34567890")
    @NotNull
    private Integer dni;

    @Schema(description = "Correo electrónico actualizado", example = "juan.perez@establecimiento.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Sueldo o remuneración base actualizada", example = "480000.00")
    @NotNull
    private Double sueldo;

    @Schema(description = "Clave Única de Identificación Tributaria (Solo caracteres numéricos)", example = "20345678909")
    @NotBlank
    @Pattern(regexp = "^[0-9]+$")
    private String cuit;

    @Schema(description = "Nuevo estado operativo del empleado dentro de la organización", implementation = EEstadoEmpleado.class)
    @NotNull
    private EEstadoEmpleado estado;

    @Schema(description = "Número telefónico de contacto actualizado (Solo números)", example = "2235123456")
    @NotBlank
    private String telefono;

    @Schema(description = "Datos de localización domiciliaria actualizados")
    @Valid
    @NotNull
    private DireccionDTO direccion;

    @Schema(description = "UUID público del nuevo rol de seguridad asignado (null si no se desea modificar)", example = "8a7b6c5d-4e3f-2a1b-0c9d-e8f7d6c5b4a3", nullable = true)
    private UUID rolPublicId;

    @Schema(description = "UUID público del nuevo sector físico u operativo asignado (null si no se desea modificar)", example = "5e4d3c2b-1a0f-9e8d-7c6b-5a4f3e2d1c0b", nullable = true)
    private UUID sectorPublicId;
}