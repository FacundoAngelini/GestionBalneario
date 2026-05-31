package com.Gestion.MiBalnearioGestion.Auth;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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

    // --- LOGIN ---
    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                input.nombreUsuario(),
                input.contrasenia()));
        return credentialsRepository.findByNombreUsuario(input.nombreUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // --- REGISTRO ---
    @Transactional
    public UsuarioDTO register(NewAccountRequest request) {

        if (credentialsRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            throw new EntidadExistenteException("El nombre de usuario ya existe", "CredencialEntity");
        }

        // 2. Crear y guardar primero el UsuarioEntity básico (vacío por ahora)
        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        // NOTA: Si tu UsuarioEntity todavía tiene los campos 'nombreUsuario' y 'contrasenia',
        // seteáselos acá temporalmente para que no tire error de "null" la BD:
        nuevoUsuario.setNombreUsuario(request.getNombreUsuario());
        nuevoUsuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));

        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // 3. Crear la CredencialEntity apuntando al usuario
        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenia(passwordEncoder.encode(request.getContrasenia())) // Encriptada para Auth
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(new HashSet<>()) // Acá después asociás tus RolesEntity
                .build();

        credentialsRepository.save(nuevaCredencial);

        // 4. Devolver el DTO del usuario
        return usuarioMapper.convertToDTO(nuevoUsuario);
    }
}