package com.Gestion.MiBalnearioGestion.Autentificacion;

import com.Gestion.MiBalnearioGestion.Clientes.*;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Empleados.*;
import com.Gestion.MiBalnearioGestion.Clientes.mappers.ClienteMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Servicio.EmpleadosRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final EmpleadosRepository empleadosRepository;

    private final ClientesRepository clientesRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UsuarioMapper usuarioMapper;

    private final ClienteMapper clienteMapper;

    private final EmpleadoMapper empleadoMapper;


    public AuthResponse registro (Registrable registrable)
    {
        if(clientesRepository.existsByEmail(registrable.getEmail()))
            throw new EntidadExistenteException("Ya existe un usuario registrado con este Email", registrable.toString());

        /** Implementar existsByEmail en EmpleadoRepository
        if(empleadosRepository.existsByEmail(registrable.getEmail()))
            throw new EntidadExistenteException("Ya existe un usuario registrado con este Email", registrable.toString());
            */

        String token = jwtService.generateToken(registrable.getUser().getNombreUsuario());

        return null;
    }

}
