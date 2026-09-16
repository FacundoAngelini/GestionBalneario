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
@Schema(description = "DTO que representa una mesa, extendiendo las propiedades básicas de un recurso")
public class MesaDTO extends RecursoDTO {

    @Schema(description = "Número identificador físico de la mesa en el establecimiento", example = "14")
    @NotNull
    @Min(1)
    private Integer numero;

    @Schema(description = "Capacidad máxima de comensales o personas sentadas en la mesa", example = "4")
    @NotNull
    @Min(1)
    private Integer capacidad;
}