package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Common.Validacion.CuitValido;
import com.Gestion.MiBalnearioGestion.Common.Validacion.TelefonoValido;
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

    @NotBlank @Size(max = 50, message = "El nombre admite hasta 50 caracteres")
    private String nombre;

    @NotBlank @Size(max = 50, message = "El apellido admite hasta 50 caracteres")
    private String apellido;

    @NotNull @Min(value = 1_000_000, message = "El DNI no es válido") @Max(value = 99_999_999, message = "El DNI no es válido")
    private Integer dni;

    @NotBlank @Size(max = 75, message = "El email no puede contener mas de 75 caracteres") @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "El email tiene un formato inválido")
    private String email;

    @NotNull @Positive(message = "El sueldo debe ser mayor a 0")
    private Double sueldo;

    @NotBlank @CuitValido
    private String cuit;

    @NotBlank @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números") @TelefonoValido
    private String telefono;

    @Valid @NotNull
    private DireccionDTO direccion;

    @Schema(description = "Nuevo sector (null = no cambiarlo)", nullable = true)
    private UUID sectorPublicId;
}