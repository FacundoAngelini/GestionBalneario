package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredencialRepositorio credentialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse authenticate(AuthRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                input.nombreUsuario(),
                input.contrasenia()));

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(input.nombreUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String accessToken = jwtService.generateToken(credencial);
        String refreshToken = jwtService.generateRefreshToken(credencial);

        credencial.setRefreshToken(refreshToken);
        credentialsRepository.save(credencial);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public UsuarioDTO register(NewAccountRequest request) {

        if (credentialsRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new EntidadExistenteException("El nombre de usuario ya existe", "CredencialEntity");
        }

        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario.setNombreUsuario(request.getNombreUsuario());
        nuevoUsuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenia(passwordEncoder.encode(request.getContrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(new HashSet<>())
                .build();

        String tokenInicial = jwtService.generateRefreshToken(nuevaCredencial);

        nuevaCredencial.setRefreshToken(tokenInicial);

        credentialsRepository.save(nuevaCredencial);

        return usuarioMapper.convertToDTO(nuevoUsuario);
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (credencial.getRefreshToken() == null || !credencial.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("El Refresh Token no coincide o es inválido");
        }

        if (!jwtService.validateRefreshToken(refreshToken, credencial)) {
            throw new IllegalArgumentException("El Refresh Token expiró o es inválido");
        }

        String newAccessToken = jwtService.generateToken(credencial);
        String newRefreshToken = jwtService.generateRefreshToken(credencial);

        credencial.setRefreshToken(newRefreshToken);
        credentialsRepository.save(credencial);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}