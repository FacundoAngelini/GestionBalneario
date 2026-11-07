package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Validacion.LimitesRecursos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
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
    @NotNull(message = "El numero no puede estar vacio")
    @Min(value = 1, message = "El numero minimo es 1")
    @Max(value = LimitesRecursos.MAX_NUMERO, message = "El número no puede superar 1000")
    private Integer numero;

    @Schema(description = "Número de pasillo o sector de ubicación de la carpa", example = "3")
    @NotNull(message = "El pasillo  no puede estar vacio")
    @Min(value = 1, message = "El numero minimo es 1")
    @Max(value = LimitesRecursos.MAX_PASILLO, message = "Los pasillos van del 1 al 20")
    private Integer pasillo;

    @Schema(description = "Capacidad máxima de personas permitidas en la carpa", example = "6")
    @NotNull(message = "El numero no puede estar vacio")
    @Min(value = 2, message = "El numero minimo es 2")
    @Max(value = LimitesRecursos.MAX_CAPACIDAD, message = "La capacidad máxima es 8 personas")
    private Integer capacidad;
}