package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PrecioRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPrecioRecursoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos-precios")
@RequiredArgsConstructor
@Tag(name = "Precio Recurso Controller", description = "Endpoints para la gestión, asignación y consulta del historial de precios de los recursos")
public class PrecioRecursoControlador {

    private final IPrecioRecursoServicio precioRecursoServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo registro de precio", description = "Asigna una tarifa monetaria a un recurso específico indicando su período de validez. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de precio creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<PrecioRecursoDTO> crearPrecio(@Valid @RequestBody PrecioRecursoDTO precioRecursoDTO) {
        return new ResponseEntity<>(precioRecursoServicio.crearPrecio(precioRecursoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un registro de precio existente", description = "Modifica los montos o las fechas de vigencia de un registro de precio mediante su UUID público. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Precio actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de precio no encontrado", content = @Content)
    })
    public ResponseEntity<PrecioRecursoDTO> actualizarPrecio(
            @Parameter(description = "UUID público del registro de precio a modificar", example = "e5b4c3d2-a1f0-4e9b-8c7d-6e5f4a3b2c1d")
            @PathVariable UUID id,
            @Valid @RequestBody PrecioRecursoDTO dto) {
        return ResponseEntity.ok(precioRecursoServicio.actualizarPrecio(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener un registro de precio por ID", description = "Recupera la información detallada de una asignación de precio utilizando su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Precio encontrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de precio no encontrado", content = @Content)
    })
    public ResponseEntity<PrecioRecursoDTO> obtenerPrecio(
            @Parameter(description = "UUID público del registro de precio a buscar", example = "e5b4c3d2-a1f0-4e9b-8c7d-6e5f4a3b2c1d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(precioRecursoServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar registros de precios", description = "Obtiene un listado de precios aplicando filtros avanzados opcionales por rangos de fechas de vigencia/caducidad y montos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de precios obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Formatos de fecha o tipos de datos de consulta incorrectos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<PrecioRecursoDTO>> obtenerPrecio(
            @Parameter(description = "Filtrar por fecha exacta de inicio de vigencia (Format: YYYY-MM-DD)", example = "2026-06-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioVigenciaIgual,
            @Parameter(description = "Filtrar precios cuya vigencia inicie antes o en la fecha dada", example = "2026-06-15") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioVigenciaMenor,
            @Parameter(description = "Filtrar precios cuya vigencia inicie después o en la fecha dada", example = "2026-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioVigenciaMayor,
            @Parameter(description = "Filtrar por fecha exacta de caducidad del precio", example = "2026-06-30") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoIgual,
            @Parameter(description = "Filtrar precios cuya caducidad sea anterior o igual a la fecha dada", example = "2026-07-15") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoMenor,
            @Parameter(description = "Filtrar precios cuya caducidad sea posterior o igual a la fecha dada", example = "2026-05-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoMayor,
            @Parameter(description = "Filtrar por un valor de precio exacto", example = "4500.00") @RequestParam(required = false) Double precioIgual,
            @Parameter(description = "Filtrar registros con precios menores o iguales al provisto", example = "6000.00") @RequestParam(required = false) Double precioMenor,
            @Parameter(description = "Filtrar registros con precios mayores o iguales al provisto", example = "2000.00") @RequestParam(required = false) Double precioMayor) {
        return ResponseEntity.ok(precioRecursoServicio.buscarTodos(precioVigenciaIgual, precioVigenciaMenor, precioVigenciaMayor, precioCaducidoIgual, precioCaducidoMenor, precioCaducidoMayor, precioIgual, precioMenor, precioMayor));
    }
}