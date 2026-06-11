package com.Gestion.MiBalnearioGestion.Usuarios.Servicio;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.function.Predicate;
public class UsuarioSpecification {

    public static PredicateSpecification<UsuarioEntity> usernameIgual(String username) {
        return (root, cb) -> username == null || username.isEmpty()
                ? cb.conjunction()
                : cb.equal(
                root.join("credencial", JoinType.LEFT).get("nombreUsuario"),
                username);
    }

    public static PredicateSpecification<UsuarioEntity> usernameContiene(String usernameContiene) {
        return (root, cb) -> usernameContiene == null || usernameContiene.isEmpty()
                ? cb.conjunction()
                : cb.like(
                root.join("credencial", JoinType.LEFT).get("nombreUsuario"),
                "%" + usernameContiene + "%");
    }

    public static PredicateSpecification<UsuarioEntity> activos(Boolean activo) {
        return (root, cb) -> activo == null
                ? cb.conjunction()
                : cb.isTrue(root.get("activo"));
    }

    public static PredicateSpecification<UsuarioEntity> noActivos(Boolean noActivos) {
        return (root, cb) -> noActivos == null
                ? cb.conjunction()
                : cb.isFalse(root.get("activo"));
    }
}

