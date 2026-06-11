package com.Gestion.MiBalnearioGestion.Auth.Autentificacion;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Mapper.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredencialRepositorio credentialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;
    private final RolesRepositorio rolesRepositorio;
    private final ClientesRepository clientesRepository;

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
    public RegisterResponse register(NewAccountRequest request) {

        if (credentialsRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new EntidadExistenteException("El nombre de usuario ya existe", "CredencialEntity");
        }

        // 1. Usuario base — solo publicId y activo, sin username ni password
        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // 2. Rol cliente
        RolesEntity rolCliente = rolesRepositorio.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("El rol ROLE_CLIENTE no existe en la base de datos"));

        // 3. Credencial vinculada — contraseña encriptada UNA sola vez
        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenia(passwordEncoder.encode(request.getContrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(new HashSet<>(Set.of(rolCliente)))
                .build();

        String tokenInicial = jwtService.generateRefreshToken(nuevaCredencial);
        nuevaCredencial.setRefreshToken(tokenInicial);
        credentialsRepository.save(nuevaCredencial);

        // 4. ClienteEntity vacío vinculado al usuario
        // El cliente completa sus datos (nombre, dni, etc.) después desde /perfil
        ClienteEntity nuevoCliente = ClienteEntity.builder()
                .usuario(nuevoUsuario)
                .estado(true)
                .build();
        clientesRepository.save(nuevoCliente);

        String accessToken = jwtService.generateToken(nuevaCredencial);

        return RegisterResponse.builder()
                .usuario(usuarioMapper.convertToDTO(nuevoUsuario))
                .token(accessToken)
                .refreshToken(tokenInicial)
                .build();
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

    @Transactional
    public void logout(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // invalidamos el refresh token — el access token expirará solo
        credencial.setRefreshToken(null);
        credentialsRepository.save(credencial);
    }
}