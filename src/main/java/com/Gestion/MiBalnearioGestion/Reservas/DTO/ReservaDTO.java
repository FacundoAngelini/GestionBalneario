package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
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
public class ReservaDTO {

    private UUID publicId;

    @NotNull
    private UUID clientePublicId;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    @NotNull
    private List<UUID> recursosPublicIds;

    private EReservaEstado estadoReserva;
}
