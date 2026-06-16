package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO que representa el historial o asignación de precio para un recurso específico en un período de tiempo")
public class PrecioRecursoDTO {

    @Schema(description = "UUID público único del registro de precio", example = "e5b4c3d2-a1f0-4e9b-8c7d-6e5f4a3b2c1d")
    private UUID publicId;

    @Schema(description = "Valor monetario asignado al recurso. Debe ser igual o mayor a 0.", example = "4500.00")
    @NotNull
    @Min(0)
    private Double precio;

    @Schema(description = "Fecha a partir de la cual el precio comienza a regir (inclusive)", example = "2026-06-01")
    @NotNull
    private LocalDate fechaVigencia;

    @Schema(description = "Fecha de finalización de la validez del precio (inclusive)", example = "2026-06-30")
    @NotNull
    private LocalDate fechaCaducada;

    @Schema(description = "UUID público del recurso al cual se le aplica este precio", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
    @NotNull
    private UUID recursoPublicId;
}