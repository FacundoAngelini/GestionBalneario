package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReservaPresencialDTO(
        @NotNull UUID clientePublicId,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotNull EModalidad modalidad,
        @NotEmpty List<UUID> recursosPublicIds,
        boolean cobrarAhora
) {}