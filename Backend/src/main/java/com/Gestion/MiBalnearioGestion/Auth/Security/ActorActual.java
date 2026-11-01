package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Identifica a quien está haciendo la petición actual (se toma del token, nunca de un dato enviado por el frontend). */
@Component
@RequiredArgsConstructor
public class ActorActual {

    public record Actor(String usuario, EmpleadoEntity empleado) {}

    private final CredencialRepositorio credencialRepositorio;

    public Actor obtener() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new Actor("sistema", null); // procesos automáticos, como el vencimiento de pendientes
        }
        EmpleadoEntity empleado = credencialRepositorio.findByNombreUsuario(auth.getName())
                .map(CredencialEntity::getUsuario)
                .map(UsuarioEntity::getEmpleado)
                .orElse(null);
        return new Actor(auth.getName(), empleado);
    }
}