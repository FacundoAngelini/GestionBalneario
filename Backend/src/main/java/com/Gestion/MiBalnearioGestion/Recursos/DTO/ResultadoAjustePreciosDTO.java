package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de un ajuste masivo de precios")
public record ResultadoAjustePreciosDTO(
        @Schema(description = "true si fue una simulación (no se guardó nada)") boolean simulado,
        @Schema(description = "Cantidad de recursos con al menos un precio modificado") int recursosAfectados,
        @Schema(description = "Cantidad de precios modificados") int preciosAfectados,
        @Schema(description = "Primeros ejemplos de antes y después") List<Ejemplo> ejemplos
) {
    public record Ejemplo(String recurso, EModalidad modalidad, double precioActual, double precioNuevo) {}
}