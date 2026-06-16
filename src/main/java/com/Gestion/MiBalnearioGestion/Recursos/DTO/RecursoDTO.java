package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@Schema(description = "DTO base que representa las propiedades comunes de cualquier recurso del sistema")
public class RecursoDTO {

    @Schema(description = "UUID público único del recurso", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
    private UUID publicId;

    @Schema(description = "Nombre identificatorio o descriptivo del recurso", example = "Carpa Standard Lateral")
    @NotBlank
    private String nombre;

    @Schema(description = "Indica si el recurso se encuentra disponible para ser reservado por los clientes", example = "true")
    @NotNull
    private Boolean esReservable;

    @Schema(description = "UUID público del sector de la organización al cual pertenece este recurso", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    @NotNull
    private UUID sectorPublicId;
}
