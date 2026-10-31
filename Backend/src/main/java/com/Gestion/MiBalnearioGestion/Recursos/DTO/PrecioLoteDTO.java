package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Schema(description = "Definición de precios aplicados a un lote según la modalidad")
@Getter
@Setter
public class PrecioLoteDTO {
    @Schema(description = "Modalidad de alquiler", example = "TEMPORADA")
    @NotNull
    private EModalidad modalidad;

    @Schema(description = "Monto del precio", example = "150000.0")
    @NotNull @Min(0)
    private Double precio;

    @Schema(description = "Fecha de inicio de vigencia del precio", example = "2026-12-01")
    @NotNull
    private LocalDate fechaVigencia;

    @Schema(description = "Fecha de caducidad del precio", example = "2027-03-31")
    @NotNull
    private LocalDate fechaCaducada;
}
