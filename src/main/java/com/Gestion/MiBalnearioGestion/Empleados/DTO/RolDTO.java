package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un rol de del empleado")
public class RolDTO {

    @Schema(description = "UUID público único del rol en el sistema", example = "8a7b6c5d-4e3f-2a1b-0c9d-e8f7d6c5b4a3")
    private UUID publicId;

    @Schema(description = "Nombre o categoría del rol que define los permisos de seguridad (Ej: MOZO, CAJERO, CLIENTE, GERENTE, ADMIN)", implementation = EtipoRol.class)
    @NotNull
    private EtipoRol tipoRol;
}