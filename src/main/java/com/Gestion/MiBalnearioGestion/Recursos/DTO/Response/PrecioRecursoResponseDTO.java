package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrecioRecursoResponseDTO {
    private UUID publicId;
    private double precio;
    private LocalDate fechaVigencia;
    private LocalDate fechaCaducada;
    private UUID recursoPublicId;
}