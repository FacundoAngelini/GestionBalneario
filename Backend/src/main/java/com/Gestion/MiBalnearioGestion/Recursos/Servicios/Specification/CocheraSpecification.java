package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class CocheraSpecification {
    public static PredicateSpecification<CocheraEntity> cocheraIgual(Integer cocheraIgual){
        return (root, cb) -> cocheraIgual == null
                ? cb.conjunction()
                : cb.equal(root.get("numeroCochera"), cocheraIgual);
    }

    public static PredicateSpecification<CocheraEntity> cocheraMenor(Integer cocheraMenor){
        return (root, cb) -> cocheraMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("numeroCochera"), cocheraMenor);
    }

    public static PredicateSpecification<CocheraEntity> cocheraMayor(Integer cocheraMayor){
        return (root, cb) -> cocheraMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("numeroCochera"), cocheraMayor);
    }
}