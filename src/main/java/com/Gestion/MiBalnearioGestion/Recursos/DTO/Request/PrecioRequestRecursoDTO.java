package com.Gestion.MiBalnearioGestion.Recursos.DTO.Request;

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
public class PrecioRequestRecursoDTO {
    @NotNull
    @Min(0)
    private double precio;
    @NotNull
    private LocalDate fechaVigencia;
    @NotNull
    private LocalDate fechaCaducada;
    @NotNull
    private UUID recursoPublicId;
}