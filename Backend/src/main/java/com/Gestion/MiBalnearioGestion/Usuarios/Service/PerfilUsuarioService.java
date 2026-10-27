package com.Gestion.MiBalnearioGestion.Usuarios.Service;

import com.Gestion.MiBalnearioGestion.Clientes.Mapper.ClienteMapper;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.PerfilUsuarioResponse;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.TipoPerfilUsuario;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerfilUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteMapper clienteMapper;
    private final EmpleadoMapper empleadoMapper;

    private static final Set<String> ROLES_EMPLEADO = Set.of("ROLE_GERENTE",
            "ROLE_EMPLEADO",
            "ROLE_CAJERO",
            "ROLE_MOZO",
            "ROLE_REPARTIDOR",
            "ROLE_ADMINISTRACION"
    );

    @Transactional(readOnly = true)
    public PerfilUsuarioResponse obtenerPerfilActual(
            String nombreUsuario
    ) {
        UsuarioEntity usuario = usuarioRepository
                .findByCredencial_NombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new CuentaNoEncontradaException(
                                "No existe una cuenta asociada al usuario: ",
                                nombreUsuario
                        )
                );

        List<String> roles = usuario.getCredencial() == null
                ? List.of()
                : usuario.getCredencial()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(rol -> rol.startsWith("ROLE_"))
                .distinct()
                .sorted()
                .toList();

        ClienteResponse cliente = usuario.getCliente() == null
                ? null
                : clienteMapper.convertToResponseDTO(
                usuario.getCliente()
        );

        EmpleadoResponseDTO empleado = usuario.getEmpleado() == null
                ? null
                : empleadoMapper.convertToResponseDTO(
                usuario.getEmpleado()
        );

        TipoPerfilUsuario tipoPerfil = resolverTipoPerfil(
                roles,
                cliente != null,
                empleado != null
        );

        return new PerfilUsuarioResponse(
                nombreUsuario,
                roles,
                tipoPerfil,
                cliente,
                empleado
        );
    }

    private TipoPerfilUsuario resolverTipoPerfil(
            List<String> roles,
            boolean tieneCliente,
            boolean tieneEmpleado
    ) {
        // La cuenta administrativa tiene prioridad como clasificación.
        // Si además posee un empleado asociado, sus datos se devuelven.
        if (roles.contains("ROLE_ADMIN")) {
            return TipoPerfilUsuario.ADMINISTRADOR;
        }

        if (tieneCliente || roles.contains("ROLE_CLIENTE")) {
            return TipoPerfilUsuario.CLIENTE;
        }

        if (tieneEmpleado
                || roles.stream().anyMatch(ROLES_EMPLEADO::contains)) {
            return TipoPerfilUsuario.EMPLEADO;
        }

        return TipoPerfilUsuario.CUENTA;
    }
}