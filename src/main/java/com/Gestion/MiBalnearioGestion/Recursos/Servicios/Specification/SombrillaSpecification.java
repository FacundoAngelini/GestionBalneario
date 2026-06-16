package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class SombrillaSpecification {
    public static PredicateSpecification<SombrillaEntity> numeroIgual(Integer numero){
        return (root, cb) -> numero == null
                ? cb.conjunction()
                : cb.equal(root.get("numero"), numero);
    }
    public static PredicateSpecification<SombrillaEntity> numeroMenor(Integer numeroMenor){
        return (root, cb) -> numeroMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("numero"), numeroMenor);
    }
    public static PredicateSpecification<SombrillaEntity> numeroMayor(Integer numeroMayor){
        return (root, cb) -> numeroMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("numero"), numeroMayor);
    }
    public static PredicateSpecification<SombrillaEntity> tamanioIgual(EtamanioSombrilla tamanio){
        return (root, cb) -> tamanio == null
                ? cb.conjunction()
                : cb.equal(root.get("tamanio"), tamanio);
    }
}