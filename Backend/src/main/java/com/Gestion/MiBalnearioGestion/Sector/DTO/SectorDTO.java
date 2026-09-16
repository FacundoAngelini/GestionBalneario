package com.Gestion.MiBalnearioGestion.Sector.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un sector de la organización")
public class SectorDTO {

    @Schema(description = "Identificador público único del sector", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID publicId;

    @Schema(description = "Nombre descriptivo del sector", example = "Recursos Humanos")
    @NotNull
    @NotBlank
    private String nombre;
}