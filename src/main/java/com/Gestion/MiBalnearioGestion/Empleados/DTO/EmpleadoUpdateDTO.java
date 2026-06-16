package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
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
public class EmpleadoUpdateDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotNull
    private Integer dni;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Double sueldo;

    @NotBlank
    @Pattern(regexp = "^[0-9]+$")
    private String cuit;

    @NotNull
    private EEstadoEmpleado estado;

    @NotBlank
    private String telefono;

    @Valid
    @NotNull
    private DireccionDTO direccion;

    private UUID rolPublicId;

    private UUID sectorPublicId;
}