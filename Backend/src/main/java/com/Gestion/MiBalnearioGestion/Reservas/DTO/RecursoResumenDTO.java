package com.Gestion.MiBalnearioGestion.Reservas.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoRecurso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Datos básicos de un recurso incluido en una reserva")
public record RecursoResumenDTO(
        @Schema(description = "UUID público del recurso") UUID publicId,
        @Schema(description = "Nombre del recurso", example = "Carpa 12") String nombre,
        @Schema(description = "Tipo de recurso (null para tipos que no se reservan desde el catálogo)") ETipoRecurso tipo
) {}
