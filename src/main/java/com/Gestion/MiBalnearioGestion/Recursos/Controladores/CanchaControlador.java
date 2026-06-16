package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICanchaServicio;
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
@RequestMapping("/api/v1/recursos-canchas")
@RestController
@Tag(name = "Cancha Controller", description = "Endpoints para la creación, actualización y consulta de canchas deportivas")
public class CanchaControlador {
    private final ICanchaServicio canchaServicio;
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva cancha", description = "Registra una nueva cancha en el sistema vinculándola a las propiedades base de un recurso. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cancha creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<CanchaDTO> crearCancha(@Valid @RequestBody CanchaDTO cancha) {
        return new ResponseEntity<>(canchaServicio.crearCancha(cancha), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una cancha existente", description = "Modifica los datos específicos de una cancha y sus atributos de recurso mediante su UUID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancha actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
    })
    public ResponseEntity<CanchaDTO> actualizarCancha(
            @Parameter(description = "UUID público de la cancha a actualizar", example = "e1b2c3d4-5f6a-7b8c-9d0e-1f2a3b4c5d6e")
            @PathVariable UUID id,
            @Valid @RequestBody CanchaDTO cancha) {
        return ResponseEntity.ok(canchaServicio.actualizarCancha(id, cancha));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener una cancha por ID", description = "Recupera la información detallada de una cancha específica mediante su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancha encontrada y devuelta con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada", content = @Content)
    })
    public ResponseEntity<CanchaDTO> obtenerCanchaId(
            @Parameter(description = "UUID público de la cancha a buscar", example = "e1b2c3d4-5f6a-7b8c-9d0e-1f2a3b4c5d6e")
            @PathVariable UUID id) {
        return ResponseEntity.ok(canchaServicio.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar canchas", description = "Obtiene una lista de canchas aplicando filtros avanzados opcionales por tipo, iluminación o rangos de capacidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de canchas obtenido con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<CanchaDTO>> obtenerCanchas(
            @Parameter(description = "Filtrar por tipo de superficie o disciplina")
            @RequestParam(required = false) ETipoCancha cancha,

            @Parameter(description = "Filtrar canchas con una capacidad exactamente igual a este valor", example = "10")
            @RequestParam(required = false) Integer capacidadIgual,

            @Parameter(description = "Filtrar canchas con una capacidad menor o igual a este valor", example = "12")
            @RequestParam(required = false) Integer capacidadMenor,

            @Parameter(description = "Filtrar canchas con una capacidad mayor o igual a este valor", example = "6")
            @RequestParam(required = false) Integer capacidadMayor,

            @Parameter(description = "Filtrar por disponibilidad de iluminación artificial")
            @RequestParam(required = false) Boolean iluminacion) {
        return ResponseEntity.ok(canchaServicio.buscarTodas(cancha, capacidadIgual, capacidadMenor, capacidadMayor, iluminacion));
    }
}

