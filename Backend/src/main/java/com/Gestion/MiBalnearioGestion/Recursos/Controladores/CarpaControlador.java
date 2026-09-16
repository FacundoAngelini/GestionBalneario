package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICarpaServicio;
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

@RequestMapping("/api/v1/recursos-carpas")
@RestController
@RequiredArgsConstructor
@Tag(name = "Carpa Controller", description = "Endpoints para la gestión y consulta de carpas de playa/balneario")
public class CarpaControlador {
    private final ICarpaServicio carpaServicio;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva carpa", description = "Registra una carpa en el sistema vinculándola a un sector. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carpa creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<CarpaDTO> crearCarpa(@Valid @RequestBody CarpaDTO carpaDTO){
        return new ResponseEntity<>(carpaServicio.crearCarpa(carpaDTO), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una carpa existente", description = "Modifica los datos físicos e identificativos de una carpa mediante su UUID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carpa actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carpa no encontrada", content = @Content)
    })
    public ResponseEntity<CarpaDTO> actualizarCarpa(
            @Parameter(description = "UUID público de la carpa a actualizar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id,
            @Valid @RequestBody CarpaDTO carpaDTO){
        return ResponseEntity.ok(carpaServicio.actualizarCarpa(carpaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener una carpa por ID", description = "Recupera la información detallada de una carpa específica usando su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carpa encontrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carpa no encontrada", content = @Content)
    })
    public ResponseEntity<CarpaDTO> obtenerCarpaId(
            @Parameter(description = "UUID público de la carpa a buscar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id){
        return ResponseEntity.ok(carpaServicio.buscarPorId(id));
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar carpas", description = "Obtiene un listado de carpas aplicando filtros avanzados opcionales por número de carpa, pasillo o capacidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de carpas obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<CarpaDTO>> obtenerCarpas(
            @Parameter(description = "Filtrar por número exacto de carpa", example = "10") @RequestParam(required = false) Integer numero,
            @Parameter(description = "Filtrar carpas cuyo número sea mayor o igual", example = "5") @RequestParam(required = false) Integer numeroMayor,
            @Parameter(description = "Filtrar carpas cuyo número sea menor o igual", example = "50") @RequestParam(required = false) Integer numeroMenor,
            @Parameter(description = "Filtrar por pasillo exacto de ubicación", example = "2") @RequestParam(required = false) Integer pasilloIgual,
            @Parameter(description = "Filtrar carpas cuyo pasillo sea mayor o igual", example = "1") @RequestParam(required = false) Integer pasilloMayor,
            @Parameter(description = "Filtrar carpas cuyo pasillo sea menor o igual", example = "4") @RequestParam(required = false) Integer pasilloMenor,
            @Parameter(description = "Filtrar por capacidad exacta de personas", example = "6") @RequestParam(required = false) Integer capacidadIgual){
        return ResponseEntity.ok(carpaServicio.buscarTodos(numero, numeroMayor, numeroMenor, pasilloIgual, pasilloMayor, pasilloMenor, capacidadIgual));
    }
}

