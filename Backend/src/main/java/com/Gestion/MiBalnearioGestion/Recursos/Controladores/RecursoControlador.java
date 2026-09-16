package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/recursos")
@RequiredArgsConstructor
@RestController
@Tag(name = "Recurso Base Controller", description = "Endpoints globales para la gestión, filtrado genérico, desactivación y borrado del inventario de recursos")
public class RecursoControlador {

    private final IRecursoServicio recursoServicio;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Buscar recurso base por ID", description = "Recupera los datos comunes de cualquier tipo de recurso utilizando su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    })
    public ResponseEntity<RecursoDTO> BuscarRecursoPorID(
            @Parameter(description = "UUID público del recurso a buscar", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
            @PathVariable UUID id){
        return ResponseEntity.ok(recursoServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar todos los recursos", description = "Obtiene una lista polimórfica o genérica de recursos aplicando filtros por coincidencia exacta o parcial de nombre, y estado de reserva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de recursos obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<RecursoDTO>> BuscarTodos(
            @Parameter(description = "Filtrar por nombre exacto del recurso", example = "Carpa Standard Lateral") @RequestParam (required = false) String nombreIgual,
            @Parameter(description = "Filtrar por recursos cuyo nombre contenga la cadena de texto provista", example = "Standard") @RequestParam (required = false) String nombreContiene,
            @Parameter(description = "Filtrar según si el recurso está marcado actualmente como reservable") @RequestParam (required = false) Boolean reservableVerdad){
        return ResponseEntity.ok(recursoServicio.buscarTodos(nombreIgual, nombreContiene, reservableVerdad));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar un recurso específico", description = "Cambia el estado de un recurso individual para que deje de estar disponible operativamente. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso desactivado con éxito (Sin contenido de respuesta)", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    })
    public ResponseEntity<RecursoDTO> desactivar(
            @Parameter(description = "UUID público del recurso a desactivar", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
            @PathVariable UUID id){
        recursoServicio.desactivarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/desactivar-todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar todo el inventario de recursos", description = "Realiza una desactivación masiva de todos los recursos registrados en el sistema. Operación crítica. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo el inventario fue desactivado con éxito", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<RecursoDTO> desactivarTodosRecursos(){
        recursoServicio.desactivarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/borrar-todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Borrar todo el inventario de recursos", description = "Realiza un borrado lógico o físico masivo de absolutamente todos los recursos del sistema. Operación destructiva de alto riesgo. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo el inventario fue eliminado del sistema con éxito", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<RecursoDTO> borrarTodos(){
        recursoServicio.borrarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Borrar un recurso específico", description = "Elimina un recurso del sistema a partir de su UUID público. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurso eliminado con éxito", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID erróneo o formato inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    })
    public ResponseEntity<RecursoDTO> borrarRecurso(
            @Parameter(description = "UUID público del recurso a eliminar", example = "f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c")
            @PathVariable UUID id){
        recursoServicio.borrarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener recursos disponibles por rango de fechas", description = "Endpoint público/general que devuelve el listado de recursos libres que un cliente puede seleccionar para reservar en un período de tiempo determinado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de recursos disponibles obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Formatos de fecha inválidos o inconsistencia en el rango (Ej: fechaFin anterior a fechaInicio)", content = @Content)
    })
    public ResponseEntity<List<RecursoDTO>> obtenerRecursosDisponibles(
            @Parameter(description = "Fecha de inicio del rango de disponibilidad (Format: YYYY-MM-DD)", example = "2026-06-16", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @Parameter(description = "Fecha de finalización del rango de disponibilidad (Format: YYYY-MM-DD)", example = "2026-06-23", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<RecursoDTO> disponibles = recursoServicio.listarDisponiblesParaElCliente(fechaInicio, fechaFin);
        return ResponseEntity.ok(disponibles);
    }
}