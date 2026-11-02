package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthResponse;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.Service.IAuthService;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.*;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Service.IUsuarioService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints públicos y de seguridad para el control de acceso, registro de usuarios, renovación de sesiones JWT y recuperación de cuentas")
public class AuthController {

    private final IAuthService authService;
    private final IUsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión de usuarios", description = "Autentica las credenciales del usuario (username/password) y retorna un par de tokens criptográficos (Access Token y Refresh Token) si el acceso es válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa. Se retornan los tokens JWT"),
            @ApiResponse( responseCode = "400", description = "Formato de payload inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciales de acceso incorrectas o usuario inexistente", content = @Content)
    })
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovación de Access Token mediante Refresh Token", description = "Permite a las aplicaciones cliente extender la sesión activa obteniendo un nuevo Access Token de corta duración sin interrumpir la experiencia del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado con éxito"),
            @ApiResponse(responseCode = "400", description = "Token de refresco mal estructurado o vacío", content = @Content),
            @ApiResponse(responseCode = "403", description = "Refresh Token inválido, corrupto o expirado. Requiere login manual", content = @Content)
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Autoregistro público de nuevos clientes", description = "Permite el alta directa y autónoma de usuarios en el sistema. Crea de forma simultánea la entidad de cliente y sus credenciales de seguridad iniciales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "211", description = "Usuario registrado con éxito en la plataforma"), // Mapea a 201 Created
            @ApiResponse(responseCode = "400", description = "Errores de validación en los datos de entrada (Contraseña débil, email inválido, etc.)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - El nombre de usuario, DNI o Email ya se encuentra registrado", content = @Content)
    })
    public ResponseEntity<UsuarioDTO> registerUser(@Valid @RequestBody NewAccountRequest newAccountRequest) {
        UsuarioDTO usuarioCreado = authService.register(newAccountRequest);
        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @PostMapping("/contrasenia-olvidada")
    @Operation(summary = "Solicitar restablecimiento de contraseña", description = "Gatilla el proceso de recuperación por olvido. Si el identificador coincide con un usuario activo, se despacha un correo electrónico con un token de verificación seguro de un solo uso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada (Por motivos de seguridad y enumeración de usuarios, la respuesta siempre es genérica)")
    })
    public ResponseEntity<ContraseniaResponseDTO> solicitarReset(
            @Valid @RequestBody ContraseniaOlvidadaDTO request) {
        usuarioService.solicitarResetContrasenia(request.nombreUsuario());
        return ResponseEntity.ok(ContraseniaResponseDTO.builder()
                .mensaje("Si el usuario existe, recibirás un email con las instrucciones.")
                .build());
    }

    @PostMapping("/resetear-contrasenia")
    @Operation(summary = "Confirmar y aplicar nueva contraseña", description = "Paso final del flujo de recuperación. Valida la vigencia del token recibido por correo e impacta la nueva contraseña en la base de datos tras verificar su nivel de complejidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada e impactada con éxito"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado o la nueva contraseña no cumple con los requisitos de seguridad", content = @Content)
    })
    public ResponseEntity<ContraseniaResponseDTO> resetearContrasenia(
            @Valid @RequestBody ResetearContraseniaDTO request) {
        usuarioService.resetearContrasenia(request.token(), request.nuevaContrasenia());
        return ResponseEntity.ok(ContraseniaResponseDTO.builder()
                .mensaje("Contraseña actualizada correctamente.")
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cierre de sesión e invalidación de token", description = "Recibe el token Bearer activo en las cabeceras HTTP y lo envía a la lista negra del servidor (Blacklist) para revocar su capacidad de acceso de forma inmediata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sesión cerrada con éxito. Token invalidado (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Cabecera Authorization ausente o mal formateada", content = @Content)
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Cabecera estándar de autenticación portadora del token JWT (Formato: Bearer <token>)", required = true, example = "Bearer eyJhbGciOiJI...")
            @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}