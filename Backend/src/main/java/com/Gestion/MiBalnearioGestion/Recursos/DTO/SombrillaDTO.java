package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
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
@Schema(description = "DTO que representa una sombrilla, extendiendo las propiedades básicas de un recurso")
public class SombrillaDTO extends RecursoDTO {

    @Schema(description = "Número identificador físico de la sombrilla", example = "18")
    @NotNull
    @Min(1)
    private Integer numero;

    @Schema(description = "Dimensión o tamaño de la sombrilla", implementation = EtamanioSombrilla.class)
    @NotNull
    private EtamanioSombrilla tamanio;
}