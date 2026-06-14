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
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
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

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    private final EmailService emailService;

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

        // 1. Validar que no exista el nombre de usuario
        if (credentialsRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new EntidadExistenteException("El nombre de usuario ya existe", "CredencialEntity");
        }

        // 2. Validar que no exista un cliente con el mismo DNI o Email (Evita duplicados de perfil)
        if (clientesRepository.findByDni(request.getDni()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese DNI", "ClienteEntity");
        }
        if (clientesRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese email", "ClienteEntity");
        }

        // 3. Crear la Identidad Base en memoria
        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        // 4. Buscar el Rol de Cliente
        RolesEntity rolCliente = rolesRepositorio.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error: El rol ROLE_CLIENTE no existe en la base de datos."));

        // 5. Construir la Credencial
        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenia(passwordEncoder.encode(request.getContrasenia())) // Encriptada para Spring Security
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(Set.of(rolCliente))
                .build();

        String tokenInicial = jwtService.generateRefreshToken(nuevaCredencial);
        nuevaCredencial.setRefreshToken(tokenInicial);

        // 6. Construir el Perfil del Cliente asignándole su propio UUID
        ClienteEntity nuevoCliente = ClienteEntity.builder()
                .publicId(UUID.randomUUID()) // Este es el UUID de negocio del Cliente
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fechaAlta(LocalDate.now())
                .estado(true)
                .usuario(nuevoUsuario) // Vinculado a la identidad base
                .build();

        // 7. Sincronizar la relación bidireccional en memoria (Vital para el Mapper)
        nuevoUsuario.setCredencial(nuevaCredencial);
        nuevoUsuario.setCliente(nuevoCliente);

        // 8. Persistir de forma ordenada en la base de datos
        usuarioRepository.save(nuevoUsuario);
        credentialsRepository.save(nuevaCredencial);
        clientesRepository.save(nuevoCliente);
        emailService.BienvenidaClienteRegistro(request);

        // 9. Mapear y retornar el DTO que contiene el "clienteId"
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

    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // invalida el refresh token — ya no puede renovar el access token
        credencial.setRefreshToken(null);
        credentialsRepository.save(credencial);
    }
}