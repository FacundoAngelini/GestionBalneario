package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IMesaServcio;
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
@RequestMapping("/api/v1/recursos-mesas")
@RequiredArgsConstructor
@Tag(name = "Mesa Controller", description = "Endpoints para la gestión y consulta de mesas del establecimiento")
public class MesaControlador {
    private final IMesaServcio mesaServcio;
    @PostMapping

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva mesa", description = "Registra una nueva mesa en el sistema asociada a su respectivo sector. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mesa creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada o parámetros de la URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<MesaDTO> crearMesa(@Valid @RequestBody MesaDTO mesaDTO){
        return new ResponseEntity<>(mesaServcio.crearMesa(mesaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una mesa existente", description = "Modifica los atributos físicos de una mesa mediante su UUID público. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o parámetros de URL inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    public ResponseEntity<MesaDTO> actualizarMesa(@Valid @RequestBody MesaDTO mesaDTO, @PathVariable UUID id){
        return ResponseEntity.ok(mesaServcio.actualizarMesa(mesaDTO, id));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO', 'CAJERO')")
    @Operation(summary = "Obtener una mesa por ID", description = "Recupera la información detallada de una mesa utilizando su UUID público. Accesible por personal autorizado (incluye CAJERO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa encontrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de URL o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    public ResponseEntity<MesaDTO> obtenerMesaId(
            @Parameter(description = "UUID público de la mesa a buscar", example = "d3b07384-d113-4c4e-9c8e-cfbd6c4e3012")
            @PathVariable UUID id){
        return ResponseEntity.ok(mesaServcio.obtenerMesaId(id));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO', 'CAJERO')")
    @Operation(summary = "Listar y filtrar mesas", description = "Obtiene un listado de mesas aplicando filtros avanzados opcionales por número físico y capacidad de comensales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de mesas obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<MesaDTO>> obtenerMesas(
            @Parameter(description = "Filtrar por número exacto de mesa", example = "14") @RequestParam (required = false) Integer numeroIgual,
            @Parameter(description = "Filtrar mesas cuyo número sea menor o igual", example = "20") @RequestParam (required = false) Integer numeroMenor,
            @Parameter(description = "Filtrar mesas cuyo número sea mayor o igual", example = "5") @RequestParam (required = false) Integer numeroMayor,
            @Parameter(description = "Filtrar por capacidad exacta de comensales", example = "4") @RequestParam (required = false) Integer capacidadIgual,
            @Parameter(description = "Filtrar mesas cuya capacidad sea menor o igual", example = "6") @RequestParam (required = false) Integer capacidadMenor,
            @Parameter(description = "Filtrar mesas cuya capacidad sea mayor o igual", example = "2") @RequestParam (required = false) Integer capacidadMayor){
        return ResponseEntity.ok(mesaServcio.obtenerMesas(numeroIgual, numeroMenor, numeroMayor, capacidadIgual, capacidadMenor, capacidadMayor));
    }

}
