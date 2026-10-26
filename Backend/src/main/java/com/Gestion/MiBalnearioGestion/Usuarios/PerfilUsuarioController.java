package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.PerfilUsuarioResponse;
import com.Gestion.MiBalnearioGestion.Usuarios.Service.PerfilUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(
        name = "Perfil de usuario",
        description = "Consulta del perfil propio de la cuenta autenticada"
)
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Consultar mi perfil",
            description = "Devuelve los datos personales asociados a la cuenta " +
                    "autenticada, sus roles y su tipo de perfil."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil recuperado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere iniciar sesión"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la cuenta asociada"
            )
    })
    public ResponseEntity<PerfilUsuarioResponse> obtenerMiPerfil(
            @Parameter(hidden = true) Principal principal
    ) {
        PerfilUsuarioResponse respuesta =
                perfilUsuarioService.obtenerPerfilActual(
                        principal.getName()
                );

        return ResponseEntity.ok(respuesta);
    }
}