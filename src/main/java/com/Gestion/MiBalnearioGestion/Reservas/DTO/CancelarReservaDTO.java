package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado para procesar la cancelación de una reserva existente")
public class CancelarReservaDTO {

    @Schema(description = "UUID público de la reserva que se desea cancelar", example = "b9f8e7d6-c5b4-a3f2-e1d0-9c8b7a6f5e4d")
    private UUID publicId;

    @Schema(description = "UUID público del cliente asociado a la reserva", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull
    private UUID clientePublicId;

    @Schema(description = "Fecha de inicio original de la reserva", example = "2026-07-01")
    @NotNull
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de finalización original de la reserva", example = "2026-07-10")
    @NotNull
    private LocalDate fechaFin;
}