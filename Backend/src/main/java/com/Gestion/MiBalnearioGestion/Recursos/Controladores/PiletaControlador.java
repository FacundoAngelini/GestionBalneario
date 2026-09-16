package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPiletaServicio;
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
@RequestMapping("/api/v1/recursos-piletas")
@RequiredArgsConstructor
@Tag(name = "Pileta Controller", description = "Endpoints para la gestión y consulta de piletas o piscinas")
public class PiletaControlador {

    private final IPiletaServicio piletaServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva pileta", description = "Registra una nueva pileta en el sistema asociada a su respectivo sector. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pileta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<PiletaDTO> crearPileta(@Valid @RequestBody PiletaDTO piletaDTO){
        return new ResponseEntity<>(piletaServicio.crearPileta(piletaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una pileta existente", description = "Modifica las características de una pileta mediante su UUID público. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pileta actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pileta no encontrada", content = @Content)
    })
    public ResponseEntity<PiletaDTO> actualizarPileta(@Valid @RequestBody PiletaDTO piletaDTO, @PathVariable UUID id){
        return ResponseEntity.ok(piletaServicio.actualizarPileta(piletaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener una pileta por ID", description = "Recupera la información detallada de una pileta utilizando su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pileta encontrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pileta no encontrada", content = @Content)
    })
    public ResponseEntity<PiletaDTO> obtenerPileta(
            @Parameter(description = "UUID público de la pileta a buscar", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
            @PathVariable UUID id){
        return ResponseEntity.ok(piletaServicio.obtenerPileta(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar piletas", description = "Obtiene un listado de piletas aplicando filtros opcionales por climatización y dimensiones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de piletas obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<PiletaDTO>> obtenerPiletas(
            @Parameter(description = "Filtrar por si la pileta es climatizada o no") @RequestParam (required = false) Boolean climatizada,
            @Parameter(description = "Filtrar piletas con un tamaño exacto", example = "50") @RequestParam (required = false) Integer tamanioIgual,
            @Parameter(description = "Filtrar piletas con un tamaño mayor o igual", example = "30") @RequestParam (required = false) Integer TamanioMayor,
            @Parameter(description = "Filtrar piletas con un tamaño menor o igual", example = "100") @RequestParam (required = false) Integer TamanioMenor){
        return ResponseEntity.ok(piletaServicio.obtenerPiletas(climatizada, tamanioIgual, TamanioMayor, TamanioMenor));
    }
}