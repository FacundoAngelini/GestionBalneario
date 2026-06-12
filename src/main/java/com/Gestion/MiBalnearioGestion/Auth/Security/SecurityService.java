package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final ClientesRepository clientesRepository;
    private final EmpleadosRepositorio empleadosRepositorio;

    public boolean esElPropioCliente(UUID publicId) {
        String usernameLogueado = obtenerUsernameActual();

        return clientesRepository.findByPublicId(publicId)
                .map(cliente -> cliente.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }


    public boolean esElPropioEmpleado(UUID publicId) {
        String usernameLogueado = obtenerUsernameActual();

        return empleadosRepositorio.findByPublicId(publicId)
                .map(empleado -> empleado.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }

    private String obtenerUsernameActual() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}