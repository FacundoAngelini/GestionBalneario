package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthResponse;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.Service.IAuthService;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.*;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IUsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody NewAccountRequest newAccountRequest) {
        // El servicio devuelve UsuarioDTO, por ende el ResponseEntity maneja UsuarioDTO
        UsuarioDTO usuarioCreado = authService.register(newAccountRequest);
        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @PostMapping("/contrasenia-olvidada")
    public ResponseEntity<ContraseniaResponseDTO> solicitarReset(
            @RequestBody ContraseniaOlvidadaDTO request) {
        usuarioService.solicitarResetContrasenia(request.nombreUsuario());
        return ResponseEntity.ok(ContraseniaResponseDTO.builder()
                .mensaje("Si el usuario existe, recibirás un email con las instrucciones.")
                .build());
    }

    @PostMapping("/resetear-contrasenia")
    public ResponseEntity<ContraseniaResponseDTO> resetearContrasenia(
            @RequestBody ResetearContraseniaDTO request) {
        usuarioService.resetearContrasenia(request.token(), request.nuevaContrasenia());
        return ResponseEntity.ok(ContraseniaResponseDTO.builder()
                .mensaje("Contraseña actualizada correctamente.")
                .build());
    }




@PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}
