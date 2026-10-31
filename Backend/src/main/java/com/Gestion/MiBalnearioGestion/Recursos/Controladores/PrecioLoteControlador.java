package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.AjustePreciosDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.ResultadoAjustePreciosDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPrecioLoteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ajuste masivo de precios", description = "Cambia de una vez los precios de muchos recursos")
@RestController
@RequestMapping("/api/v1/recursos-precios/ajuste-masivo")
@RequiredArgsConstructor
public class PrecioLoteControlador {

    private final IPrecioLoteServicio servicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ajustar precios en forma masiva",
            description = "Sube o baja un porcentaje, o fija un monto, a los precios vigentes y futuros de los recursos elegidos. "
                    + "Con soloSimular=true solo calcula y devuelve el resultado. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajuste calculado o aplicado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<ResultadoAjustePreciosDTO> ajustar(@Valid @RequestBody AjustePreciosDTO dto) {
        return ResponseEntity.ok(servicio.ajustar(dto));
    }
}