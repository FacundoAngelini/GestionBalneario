package com.Gestion.MiBalnearioGestion.Clientes.Service;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class ClienteSpecification {
    public static PredicateSpecification<ClienteEntity> nombreIgual(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.equal(cb.lower(root.get("nombre")), valor.toLowerCase());
    }

    public static PredicateSpecification<ClienteEntity> nombreContiene(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.like(cb.lower(root.get("nombre")),
                        "%" + valor.toLowerCase() + "%");
    }

    public static PredicateSpecification<ClienteEntity> apellidoIgual(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.equal(cb.lower(root.get("apellido")), valor.toLowerCase());
    }

    public static PredicateSpecification<ClienteEntity> apellidoContiene(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.like(cb.lower(root.get("apellido")),
                        "%" + valor.toLowerCase() + "%");
    }

    public static PredicateSpecification<ClienteEntity> dniIgual(Integer valor) {
        return valor == null ? null :
                (root, cb) -> cb.equal(root.get("dni"), valor);
    }

    public static PredicateSpecification<ClienteEntity> emailContiene(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.like(cb.lower(root.get("email")),
                        "%" + valor.toLowerCase() + "%");
    }

    public static PredicateSpecification<ClienteEntity> telefonoIgual(String valor) {
        return valor == null ? null :
                (root, cb) -> cb.equal(root.get("telefono"), valor);
    }

    public static PredicateSpecification<ClienteEntity> estadoIgual(Boolean valor) {
        return valor == null ? null :
                (root, cb) -> cb.equal(root.get("estado"), valor);
    }

    /** Un solo texto contra nombre, apellido, nombre completo, email, teléfono y DNI. */
    public static PredicateSpecification<ClienteEntity> textoContiene(String texto) {
        return (root, cb) -> {
            if (texto == null || texto.isBlank()) return cb.conjunction();
            String patron = "%" + texto.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.<String>get("nombre")), patron),
                    cb.like(cb.lower(root.<String>get("apellido")), patron),
                    cb.like(cb.lower(cb.concat(cb.concat(root.<String>get("nombre"), " "), root.<String>get("apellido"))), patron),
                    cb.like(cb.lower(root.<String>get("email")), patron),
                    cb.like(root.<String>get("telefono"), patron),
                    cb.like(root.get("dni").as(String.class), patron));
        };
    }

}
