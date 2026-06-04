package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class CocheraSpecification {
    public static PredicateSpecification<CocheraEntity> cocherIgual(Integer cocheraIgual){
        return (root,cb)-> cocheraIgual==null
                ?cb.conjunction()
                :cb.equal(root.get("numero_cochera"), cocheraIgual);
    }

    public static PredicateSpecification<CocheraEntity> cocherMenor(Integer cocheraMenor){
        return (root,cb)-> cocheraMenor==null
                ?cb.conjunction()
                :cb.lessThan(root.get("numero_cochera"), cocheraMenor);
    }

    public static PredicateSpecification<CocheraEntity> cocheraMayor(Integer cocheraMayor){
        return (root,cb)-> cocheraMayor==null
                ?cb.conjunction()
                :cb.lessThan(root.get("numero_cochera"), cocheraMayor);
    }
}
