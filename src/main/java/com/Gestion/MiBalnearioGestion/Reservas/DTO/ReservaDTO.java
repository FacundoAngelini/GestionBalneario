package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa la información detallada y requerida de una reserva")
public class ReservaDTO {

    @Schema(description = "UUID público único de la reserva", example = "d3b07384-d113-4c4e-9c8e-cfbd6c4e3012")
    private UUID publicId;

    @Schema(description = "UUID público del cliente que realiza la reserva", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull
    private UUID clientePublicId;

    @Schema(description = "Fecha en la que inicia la reserva", example = "2026-08-15")
    @NotNull
    private LocalDate fechaInicio;

    @Schema(description = "Fecha en la que finaliza la reserva", example = "2026-08-22")
    @NotNull
    private LocalDate fechaFin;

    @Schema(description = "Lista de UUIDs públicos de los recursos asignados a la reserva",
            example = "[\"e1b2c3d4-5f6a-7b8c-9d0e-1f2a3b4c5d6e\", \"f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c\"]")
    @NotNull
    private List<UUID> recursosPublicIds;

    @Schema(description = "Estado actual en el que se encuentra la reserva", implementation = EReservaEstado.class)
    private EReservaEstado estadoReserva;
}
