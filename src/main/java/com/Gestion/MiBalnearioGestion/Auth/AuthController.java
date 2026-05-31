package com.Gestion.MiBalnearioGestion.Auth;

import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        // jwtService asume que recibe un UserDetails (que CredencialEntity ya implementa)
        String token =  jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody NewAccountRequest newAccountRequest){
        // LLAMAMOS AL AUTH SERVICE
        return new ResponseEntity<>(authService.register(newAccountRequest), HttpStatus.CREATED);
    }
}
