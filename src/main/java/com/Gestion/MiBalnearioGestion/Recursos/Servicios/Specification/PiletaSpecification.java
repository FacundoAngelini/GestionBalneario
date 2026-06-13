package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class PiletaSpecification {
    public static PredicateSpecification<PiletaEntity> climatizada(Boolean climatizada){
        return (root, cb) -> climatizada == null
                ? cb.conjunction()
                : cb.equal(root.get("esClimatizada"), climatizada);
    }

    public static PredicateSpecification<PiletaEntity> tamanioIgual(Integer tamanio){
        return (root, cb) -> tamanio == null
                ? cb.conjunction()
                : cb.equal(root.get("tamanio"), tamanio);
    }

    public static PredicateSpecification<PiletaEntity> tamanioMayor(Integer tamanioMayor){
        return (root, cb) -> tamanioMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("tamanio"), tamanioMayor);
    }

    public static PredicateSpecification<PiletaEntity> tamanioMenor(Integer tamanioMenor){
        return (root, cb) -> tamanioMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("tamanio"), tamanioMenor);
    }
}

