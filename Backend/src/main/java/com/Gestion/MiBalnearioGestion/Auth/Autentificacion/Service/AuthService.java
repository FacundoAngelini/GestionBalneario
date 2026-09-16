package com.Gestion.MiBalnearioGestion.Auth.Autentificacion.Service;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthResponse;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Mapper.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
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
    @Override
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
    @Override
    public UsuarioDTO register(NewAccountRequest request) {

        if (credentialsRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new CuentaEncontradaException("El nombre de usuario ya existe", "CredencialEntity");
        }
        if (clientesRepository.findByDni(request.getDni()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese DNI", "ClienteEntity");
        }
        if (clientesRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese email", "ClienteEntity");
        }

        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        RolesEntity rolCliente = rolesRepositorio.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new DatosInvalidoException("Error: El rol ROLE_CLIENTE no existe en la base de datos.", "Auth"));

        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenia(passwordEncoder.encode(request.getContrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(Set.of(rolCliente))
                .build();

        String tokenInicial = jwtService.generateRefreshToken(nuevaCredencial);
        nuevaCredencial.setRefreshToken(tokenInicial);
        ClienteEntity nuevoCliente = ClienteEntity.builder()
                .publicId(UUID.randomUUID())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fechaAlta(LocalDate.now())
                .estado(true)
                .usuario(nuevoUsuario)
                .build();

        nuevoUsuario.setCredencial(nuevaCredencial);
        nuevoUsuario.setCliente(nuevoCliente);

        usuarioRepository.save(nuevoUsuario);
        credentialsRepository.save(nuevaCredencial);
        clientesRepository.save(nuevoCliente);

        emailService.BienvenidaClienteRegistro(request)
                .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", request.getEmail()))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email de confirmación de pago a: {}", request.getEmail(), throwable);
                    return null;
                });

        return usuarioMapper.convertToDTO(nuevoUsuario);
    }


    @Transactional
    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new CuentaNoEncontradaException("Usuario no encontrado", "CredencialEntity"));

        if (credencial.getRefreshToken() == null || !credencial.getRefreshToken().equals(refreshToken)) {
            throw new DatosInvalidoException("El Refresh Token no coincide o es inválido", "CredencialEntity");
        }

        if (!jwtService.validateRefreshToken(refreshToken, credencial)) {
            throw new DatosInvalidoException("El Refresh Token expiró o es inválido", "CredencialEntity");
        }

        String newAccessToken = jwtService.generateToken(credencial);
        String newRefreshToken = jwtService.generateRefreshToken(credencial);

        credencial.setRefreshToken(newRefreshToken);
        credentialsRepository.save(credencial);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new DatosInvalidoException("Token inválido", "CredencialEntity");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        CredencialEntity credencial = credentialsRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new CuentaNoEncontradaException("Usuario no encontrado", "Credencialentity"));
        credencial.setRefreshToken(null);
        credentialsRepository.save(credencial);
    }
}