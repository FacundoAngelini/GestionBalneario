package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/** Administrador (3) > Gerente (2) > resto (1). Una sola fuente de verdad para quién puede ver y tocar a quién. */
@Component
public class JerarquiaEmpleados {

    public static final int NIVEL_BASE = 1;
    public static final int NIVEL_GERENTE = 2;
    public static final int NIVEL_ADMIN = 3;

    public int nivelDe(String rolSeguridad) {
        if ("ROLE_ADMIN".equals(rolSeguridad)) return NIVEL_ADMIN;
        if ("ROLE_GERENTE".equals(rolSeguridad)) return NIVEL_GERENTE;
        return NIVEL_BASE;
    }

    /** Nivel de quien hace la petición, según los roles de su token. */
    public int nivelActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return NIVEL_BASE;
        Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (roles.contains("ROLE_ADMIN")) return NIVEL_ADMIN;
        if (roles.contains("ROLE_GERENTE")) return NIVEL_GERENTE;
        return NIVEL_BASE;
    }

    /** Se ve a los del mismo nivel o inferiores. */
    public boolean puedeVer(int actor, int objetivo) {
        return objetivo <= actor;
    }

    /** Solo se modifica, da de baja o reactiva a los de nivel inferior: los administradores quedan intocables. */
    public boolean puedeModificar(int actor, int objetivo) {
        return objetivo < actor;
    }

    /** El administrador crea cualquier rol; el gerente, solo los de menor rango. */
    public boolean puedeCrear(int actor, int nuevo) {
        return actor == NIVEL_ADMIN || nuevo < actor;
    }
}