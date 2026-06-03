package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Auth.*;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody NewAccountRequest newAccountRequest){
        return new ResponseEntity<>(authService.register(newAccountRequest), HttpStatus.CREATED);
    }

}
