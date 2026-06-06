package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;

public class PrecioRecursoSpecification {
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaIgual(LocalDate fechaVigenciaIgual) {
        return (root, cb) -> fechaVigenciaIgual == null
                ?cb.conjunction()
                :cb.equal(root.get("fechaVigencia"), fechaVigenciaIgual);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaMenor(LocalDate fechaVigenciaMenor) {
        return (root, cb) -> fechaVigenciaMenor == null
                ?cb.conjunction()
                :cb.greaterThan(root.get("fechaVigencia"), fechaVigenciaMenor);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaMayor(LocalDate fechaVigenciaMayor) {
        return (root, cb) -> fechaVigenciaMayor == null
                ?cb.conjunction()
                :cb.lessThan(root.get("fechaVigencia"), fechaVigenciaMayor);
    }

    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaIgual(LocalDate fechaCaducadaIgual) {
        return (root, cb) -> fechaCaducadaIgual == null
                ?cb.conjunction()
                :cb.equal(root.get("fechaCaducada"), fechaCaducadaIgual);
    }

    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaMenor(LocalDate fechaCaducadaMenor) {
        return (root, cb) -> fechaCaducadaMenor == null
                ?cb.conjunction()
                :cb.greaterThan(root.get("fechaCaducada"), fechaCaducadaMenor);
    }

    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaMayor(LocalDate fechaCaducadaMayor) {
        return (root, cb) -> fechaCaducadaMayor == null
                ?cb.conjunction()
                :cb.lessThan(root.get("fechaCaducada"), fechaCaducadaMayor);
    }

    public static PredicateSpecification<PrecioRecursoEntity> precioIgual(Double precioIgual){
        return (root,cb)-> precioIgual==null
                ?cb.conjunction()
                :cb.equal(root.get("precio"), precioIgual);
    }
    public static PredicateSpecification<PrecioRecursoEntity> precioMenor(Double precioMenor){
        return (root,cb)-> precioMenor==null
                ?cb.conjunction()
                :cb.greaterThan(root.get("precio"), precioMenor);
    }
    public static PredicateSpecification<PrecioRecursoEntity> precioMayor(Double precioMayor){
        return (root,cb)-> precioMayor==null
                ?cb.conjunction()
                :cb.lessThan(root.get("precio"), precioMayor);
    }


}
