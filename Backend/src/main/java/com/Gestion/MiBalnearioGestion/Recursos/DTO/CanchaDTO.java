package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "DTO que representa una cancha, extendiendo las propiedades básicas de un recurso")
public class CanchaDTO extends RecursoDTO {

    @Schema(description = "Tipo de superficie o disciplina de la cancha", implementation = ETipoCancha.class)
    @NotNull
    private ETipoCancha tipoCancha;

    @Schema(description = "Capacidad o cantidad recomendada de jugadores para la cancha", example = "10")
    @Min(2)
    @NotNull
    private Integer capacidad;

    @Schema(description = "Indica si la cancha cuenta con sistema de iluminación artificial para juego nocturno", example = "true")
    @NotNull
    private Boolean iluminacion;
}
