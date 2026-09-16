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
}
