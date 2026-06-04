package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class MesaSpecification {
    public static PredicateSpecification<MesaEntity> numeroIgual(Integer numeroIgual){
        return (root,cb)->numeroIgual==numeroIgual
                ?cb.conjunction()
                :cb.equal(root.get("numero"), numeroIgual);
    }
    public static PredicateSpecification<MesaEntity> numeroMenor(Integer numeroMenor){
        return (root,cb)->numeroMenor==numeroMenor
                ?cb.conjunction()
                :cb.lessThan(root.get("numero"), numeroMenor);
    }
    public static PredicateSpecification<MesaEntity> numeroMayor(Integer numeroMayor){
        return (root,cb)->numeroMayor==numeroMayor
                ?cb.conjunction()
                :cb.greaterThan(root.get("numero"), numeroMayor);
    }

    public static PredicateSpecification<MesaEntity> capacidadIgual(Integer capacidadIgual){
        return (root,cb)->capacidadIgual==capacidadIgual
                ?cb.conjunction()
                :cb.equal(root.get("capacidad"), capacidadIgual);
    }

    public static PredicateSpecification<MesaEntity> capacidadMenor(Integer capacidadMenor){
        return (root,cb)->capacidadMenor==capacidadMenor
                ?cb.conjunction()
                :cb.lessThan(root.get("capacidad"), capacidadMenor);
    }
    public static PredicateSpecification<MesaEntity> capacidadMayor(Integer capacidadMayor){
        return (root,cb)->capacidadMayor==capacidadMayor
                ?cb.conjunction()
                :cb.greaterThan(root.get("capacidad"), capacidadMayor);
    }

}
