package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoLoteDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoLoteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// ... tus otros imports

@Tag(name = "Lote de Recursos", description = "Endpoints para la creación masiva de recursos (Carpas, Sombrillas, Cocheras)")
@RestController
@RequestMapping("/api/v1/recursos/lote")
@RequiredArgsConstructor
public class RecursoLoteControlador {

    private final IRecursoLoteServicio servicio;

    @Schema(description = "Respuesta tras la creación de un lote de recursos")
    public record LoteResponse(
            @Schema(description = "Cantidad de recursos creados exitosamente", example = "10")
            int creados
    ) {}

    @Operation(
            summary = "Crear un nuevo lote",
            description = "Registra un nuevo lote en el sistema a partir de los datos proporcionados. Requiere rol de administrador.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Lote creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (Error de validación en el DTO)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado (Falta token JWT o es inválido)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado (El usuario no tiene el rol ADMIN)",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoteResponse> crear(@Valid @RequestBody RecursoLoteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoteResponse(servicio.crearLote(dto)));
    }
}