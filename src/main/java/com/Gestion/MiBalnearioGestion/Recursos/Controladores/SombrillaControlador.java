package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ISombrillaServicio;
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
@RequestMapping("/api/v1/recursos-sombrillas")
@RequiredArgsConstructor
@Tag(name = "Sombrilla Controller", description = "Endpoints para la gestión y consulta de sombrillas de playa/balneario")
public class SombrillaControlador {

    private final ISombrillaServicio sombrillaServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva sombrilla", description = "Registra una nueva sombrilla en el sistema asociada a su respectivo sector. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sombrilla creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<SombrillaDTO> crearSombrilla(@Valid @RequestBody SombrillaDTO sombrillaDTO) {
        return new ResponseEntity<>(sombrillaServicio.crearSombrilla(sombrillaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una sombrilla existente", description = "Modifica los datos específicos de una sombrilla mediante su UUID público. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sombrilla actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sombrilla no encontrada", content = @Content)
    })
    public ResponseEntity<SombrillaDTO> actualizarSombrilla(@Valid @RequestBody SombrillaDTO sombrillaDTO, @PathVariable UUID id) {
        return ResponseEntity.ok(sombrillaServicio.actualizarSombrilla(sombrillaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener una sombrilla por ID", description = "Recupera la información detallada de una sombrilla utilizando su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sombrilla encontrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sombrilla no encontrada", content = @Content)
    })
    public ResponseEntity<SombrillaDTO> obtenerSombrilla(
            @Parameter(description = "UUID público de la sombrilla a buscar", example = "7b4c3d2e-1a0f-4e9b-8c7d-6e5f4a3b2c1d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(sombrillaServicio.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar sombrillas", description = "Obtiene un listado de sombrillas aplicando filtros opcionales por numeración física y tamaño.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de sombrillas obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<SombrillaDTO>> obtenerSombrillas(
            @Parameter(description = "Filtrar por número exacto de sombrilla", example = "18") @RequestParam(required = false) Integer numero,
            @Parameter(description = "Filtrar sombrillas cuyo número sea menor o igual", example = "30") @RequestParam(required = false) Integer numeroMenor,
            @Parameter(description = "Filtrar sombrillas cuyo número sea mayor o igual", example = "5") @RequestParam(required = false) Integer numeroMayor,
            @Parameter(description = "Filtrar por la dimensión o categoría de tamaño de la sombrilla") @RequestParam(required = false) EtamanioSombrilla tamanio) {
        return ResponseEntity.ok(sombrillaServicio.buscarTodas(numero, numeroMenor, numeroMayor, tamanio));
    }
}
