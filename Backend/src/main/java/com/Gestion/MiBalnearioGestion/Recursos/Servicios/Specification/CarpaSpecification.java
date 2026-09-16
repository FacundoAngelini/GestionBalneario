package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class CarpaSpecification {
    public static PredicateSpecification<CarpaEntity> numeroIgual(Integer numero){
        return (root, cb) -> numero == null
                ? cb.conjunction()
                : cb.equal(root.get("numero"), numero);
    }

    public static PredicateSpecification<CarpaEntity> numeroMenor(Integer numeroMenor){
        return (root, cb) -> numeroMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("numero"), numeroMenor);
    }

    public static PredicateSpecification<CarpaEntity> numeroMayor(Integer numeroMayor){
        return (root, cb) -> numeroMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("numero"), numeroMayor);
    }

    public static PredicateSpecification<CarpaEntity> pasilloIgual(Integer pasillo){
        return (root, cb) -> pasillo == null
                ? cb.conjunction()
                : cb.equal(root.get("pasillo"), pasillo);
    }

    public static PredicateSpecification<CarpaEntity> pasilloMayor(Integer pasilloMayor){
        return (root, cb) -> pasilloMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("pasillo"), pasilloMayor);
    }

    public static PredicateSpecification<CarpaEntity> pasilloMenor(Integer pasilloMenor){
        return (root, cb) -> pasilloMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("pasillo"), pasilloMenor);
    }

    public static PredicateSpecification<CarpaEntity> capacidadIgual(Integer capacidad){
        return (root, cb) -> capacidad == null
                ? cb.conjunction()
                : cb.equal(root.get("capacidad"), capacidad);
    }
}