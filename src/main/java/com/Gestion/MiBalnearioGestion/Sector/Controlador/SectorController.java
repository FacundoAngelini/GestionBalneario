package com.Gestion.MiBalnearioGestion.Sector.Controlador;

import com.Gestion.MiBalnearioGestion.Sector.DTO.SectorDTO;
import com.Gestion.MiBalnearioGestion.Sector.Servicios.ISectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sectores")
@RequiredArgsConstructor
@Tag(name = "Sector Controller", description = "Endpoints para la gestión de sectores o áreas de la organización (Requiere rol ADMIN)")
public class SectorController {

    private final ISectorService sectorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los sectores", description = "Obtiene una lista con todos los sectores registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sectores obtenida con éxito"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<List<SectorDTO>> listarTodos() {
        return ResponseEntity.ok(sectorService.listarTodos());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar un sector por ID", description = "Obtiene los detalles de un sector específico mediante su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sector encontrado con éxito"),
            @ApiResponse(responseCode = "404", description = "Sector no encontrado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<SectorDTO> buscarPorId(
            @Parameter(description = "UUID público del sector a buscar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(sectorService.buscarPorId(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo sector", description = "Registra un nuevo sector en el sistema a partir de los datos provistos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sector creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<SectorDTO> crearSector(@Valid @RequestBody SectorDTO dto) {
        return new ResponseEntity<>(sectorService.crearSector(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un sector existente", description = "Modifica los datos de un sector identificado por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sector actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sector no encontrado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<SectorDTO> actualizarSector(
            @Parameter(description = "UUID público del sector a actualizar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id,
            @Valid @RequestBody SectorDTO dto) {
        return ResponseEntity.ok(sectorService.actualizarSector(id, dto));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un sector", description = "Borra físicamente o deshabilita un sector del sistema mediante su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sector eliminado con éxito", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sector no encontrado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<Void> borrarSector(
            @Parameter(description = "UUID público del sector a eliminar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id) {
        sectorService.borrarSector(id);
        return ResponseEntity.noContent().build();
    }
}