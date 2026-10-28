package com.Gestion.MiBalnearioGestion.Reservas.Controlador;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.TemporadaValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Temporada", description = "Endpoints para la gestión y consulta de la temporada del balneario")
@RestController
@RequestMapping("/api/v1/temporada")
@RequiredArgsConstructor
public class TemporadaController {

    private final TemporadaValidator temporadaValidator;

    @Schema(description = "Fechas de inicio y fin de la temporada configurada")
    public record TemporadaDTO(
            @Schema(example = "2026-12-01") LocalDate inicioTemporada,
            @Schema(example = "2027-03-31") LocalDate finTemporada) {}

    @Operation(
            summary = "Obtener configuración de temporada",
            description = "Devuelve las fechas de inicio y fin de la temporada vigente en el sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Temporada obtenida correctamente"
    )
    @GetMapping
    public TemporadaDTO obtener() {
        var c = temporadaValidator.obtenerConfiguracionVigente();
        return new TemporadaDTO(c.getInicioTemporada(), c.getFin_temporada());
    }
}