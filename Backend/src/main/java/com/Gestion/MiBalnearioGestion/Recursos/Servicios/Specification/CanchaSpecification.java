package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class CanchaSpecification {
    public static PredicateSpecification<CanchaEntity> tipoDeCancha(ETipoCancha tipoCancha) {
        return (root, cb) -> tipoCancha == null
                ? cb.conjunction()
                : cb.equal(root.get("tipoCancha"), tipoCancha);
    }

    public static PredicateSpecification<CanchaEntity> capacidadIgual(Integer capacidad) {
        return (root, cb) -> capacidad == null
                ? cb.conjunction()
                : cb.equal(root.get("capacidad"), capacidad);
    }

    public static PredicateSpecification<CanchaEntity> capacidadMenor(Integer capacidadMenor) {
        // Menor que la capacidad máxima solicitada
        return (root, cb) -> capacidadMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("capacidad"), capacidadMenor);
    }

    public static PredicateSpecification<CanchaEntity> capacidadMayor(Integer capacidadMayor) {
        // Mayor que la capacidad mínima solicitada
        return (root, cb) -> capacidadMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("capacidad"), capacidadMayor);
    }

    public static PredicateSpecification<CanchaEntity> iluminacion(Boolean iluminacion) {
        return (root, cb) -> iluminacion == null
                ? cb.conjunction()
                : cb.equal(root.get("iluminacion"), iluminacion);
    }
}