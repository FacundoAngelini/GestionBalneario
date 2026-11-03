package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Common.Validacion.CuitValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos que el administrador o el gerente pueden modificar de un empleado")
public class EmpleadoUpdateDTO {

    @NotBlank private String nombre;
    @NotBlank private String apellido;

    @NotNull @Min(value = 1_000_000, message = "El DNI no es válido") @Max(value = 99_999_999, message = "El DNI no es válido")
    private Integer dni;

    @NotBlank @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "El email tiene un formato inválido")
    private String email;

    @NotNull @Positive(message = "El sueldo debe ser mayor a 0")
    private Double sueldo;

    @NotBlank @CuitValido
    private String cuit;

    @NotBlank @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números") @Size(min = 9, max = 15)
    private String telefono;

    @Valid @NotNull
    private DireccionDTO direccion;

    @Schema(description = "Nuevo sector (null = no cambiarlo)", nullable = true)
    private UUID sectorPublicId;
}