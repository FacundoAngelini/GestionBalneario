package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "DTO que representa una pileta o piscina, extendiendo las propiedades básicas de un recurso")
public class PiletaDTO extends RecursoDTO {

    @Schema(description = "Indica si la pileta cuenta con sistema de climatización de agua", example = "true")
    @NotNull
    private Boolean esClimatizada;

    @Schema(description = "Tamaño, superficie en metros cuadrados o capacidad estimada de la pileta", example = "50")
    @NotNull
    private Integer tamanio;
}