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
@Schema(description = "DTO que representa una cochera, extendiendo las propiedades básicas de un recurso")
public class CocheraDTO extends RecursoDTO {

    @Schema(description = "Número identificador físico de la plaza de estacionamiento / cochera", example = "105")
    @NotNull
    @Min(1)
    private Integer numeroCochera;
}