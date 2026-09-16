package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "DTO que representa una carpa, extendiendo las propiedades básicas de un recurso")
public class CarpaDTO extends RecursoDTO {

    @Schema(description = "Número identificador físico de la carpa", example = "42")
    @NotNull
    @Min(1)
    private Integer numero;

    @Schema(description = "Número de pasillo o sector de ubicación de la carpa", example = "3")
    @NotNull
    @Min(1)
    private Integer pasillo;

    @Schema(description = "Capacidad máxima de personas permitidas en la carpa", example = "6")
    @NotNull
    @Min(2)
    private Integer capacidad;
}