package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
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
public class RolDTO {
    private UUID publicId;
    @NotNull
    private EtipoRol rol;
}
