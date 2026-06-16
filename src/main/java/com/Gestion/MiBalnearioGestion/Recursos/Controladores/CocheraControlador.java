package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICocheraServicio;
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

@RequiredArgsConstructor
@RequestMapping("/api/v1/recursos-cocheras")
@RestController
@Tag(name = "Cochera Controller", description = "Endpoints para la gestión y consulta de espacios de estacionamiento (cocheras)")
public class CocheraControlador {
    private final ICocheraServicio cocheraServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva cochera", description = "Registra una nueva plaza de cochera en el sistema. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cochera creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<CocheraDTO> crearCochera(@Valid @RequestBody CocheraDTO dto) {
        return new ResponseEntity<>(cocheraServicio.crearCochera(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Actualizar una cochera existente", description = "Modifica los datos de una cochera mediante su UUID. Accesible por personal autorizado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cochera actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cochera no encontrada", content = @Content)
    })
    public ResponseEntity<CocheraDTO> actualizarCochera(
            @Parameter(description = "UUID público de la cochera a actualizar", example = "e5b4c3d2-a1f0-4e9b-8c7d-6e5f4a3b2c1d")
            @PathVariable UUID id,
            @Valid @RequestBody CocheraDTO dto) {
        return ResponseEntity.ok(cocheraServicio.actualizarCochera(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Buscar una cochera por ID", description = "Recupera la información detallada de una cochera específica usando su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cochera encontrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cochera no encontrada", content = @Content)
    })
    public ResponseEntity<CocheraDTO> buscarCochera(
            @Parameter(description = "UUID público de la cochera a consultar", example = "e5b4c3d2-a1f0-4e9b-8c7d-6e5f4a3b2c1d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(cocheraServicio.buscarCochera(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar cocheras", description = "Obtiene un listado de cocheras aplicando filtros opcionales por número físico de plaza.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de cocheras obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<CocheraDTO>> obtenerCocheras(
            @Parameter(description = "Filtrar por número exacto de cochera", example = "105") @RequestParam(required = false) Integer cocheraIgual,
            @Parameter(description = "Filtrar cocheras cuyo número sea menor o igual al valor provisto", example = "200") @RequestParam(required = false) Integer cocheraMenor,
            @Parameter(description = "Filtrar cocheras cuyo número sea mayor o igual al valor provisto", example = "50") @RequestParam(required = false) Integer cocheraMayor) {
        return ResponseEntity.ok(cocheraServicio.listarCocheras(cocheraIgual, cocheraMenor, cocheraMayor));
    }
}
