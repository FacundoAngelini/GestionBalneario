package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelarReservaDTO {
    @NotNull
    private UUID publicId;
    @NotNull
    private UUID clientePublicId;
    @NotNull
    private LocalDate fechaInicio;
    @NotNull
    private LocalDate fechaFin;
}