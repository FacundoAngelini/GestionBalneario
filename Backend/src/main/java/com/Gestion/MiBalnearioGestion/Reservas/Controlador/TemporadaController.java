package com.Gestion.MiBalnearioGestion.Reservas.Controlador;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.TemporadaValidator;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ITemporadaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Temporada", description = "Endpoints para la gestión y consulta de la temporada del balneario")
@RestController
@RequestMapping("/api/v1/temporada")
@RequiredArgsConstructor
public class TemporadaController {

    private final TemporadaValidator temporadaValidator;
    private final ITemporadaServicio temporadaServicio;


    @Schema(description = "Fechas de inicio y fin de la temporada configurada")
    public record TemporadaDTO(
            @NotNull @Schema(example = "2026-12-01") LocalDate inicioTemporada,
            @NotNull @Schema(example = "2027-03-31") LocalDate finTemporada) {}

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
    @Operation(
            summary = "Configurar la temporada",
    description = "Crea la nueva configuración de temporada vigente. Requiere rol ADMIN."
            )
    @ApiResponse(responseCode = "200", description = "Temporada guardada correctamente")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TemporadaDTO guardar(@Valid @RequestBody TemporadaDTO dto) {
        var c = temporadaServicio.guardar(dto.inicioTemporada(), dto.finTemporada());
        return new TemporadaDTO(c.getInicioTemporada(), c.getFin_temporada());
    }
}
