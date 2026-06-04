package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class CanchaSpecification {
    public static PredicateSpecification<CanchaEntity> tipoDeCancha(ETipoCancha tipoCancha) {
        return (root,cb)-> tipoCancha==null
                ?cb.conjunction()
                :cb.equal(root.get("tipoCancha"), tipoCancha);
    }

    public static PredicateSpecification<CanchaEntity> capacidadIgual(Integer capacidadIgual) {
        return(root,cb)-> capacidadIgual==null
                ?cb.conjunction()
                :cb.equal(root.get("capacidad"), capacidadIgual);
    }

    public static PredicateSpecification<CanchaEntity> capacidadMenor(Integer capacidadMenor) {
        return(root,cb)-> capacidadMenor==null
                ?cb.conjunction()
                :cb.greaterThan(root.get("capacidad"), capacidadMenor);
    }

    public static PredicateSpecification<CanchaEntity> capacidadMayor(Integer capacidadMayor) {
        return(root,cb)-> capacidadMayor==null
                ?cb.conjunction()
                :cb.lessThan(root.get("capacidad"), capacidadMayor);
    }

    public static PredicateSpecification<CanchaEntity> iluminacion(boolean iluminacion) {
        return (root,cb)-> cb.equal(root.get("true"), iluminacion);
    }
    public static PredicateSpecification<CanchaEntity> Noiluminacion(boolean iluminacion) {
        return (root,cb)-> cb.equal(root.get("false"), iluminacion);
    }



}
