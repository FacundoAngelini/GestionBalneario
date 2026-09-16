package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;

public class PrecioRecursoSpecification {
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaIgual(LocalDate fecha) {
        return (root, cb) -> fecha == null
                ? cb.conjunction()
                : cb.equal(root.get("fechaVigencia"), fecha);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaMenor(LocalDate fechaMenor) {
        return (root, cb) -> fechaMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("fechaVigencia"), fechaMenor);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaVigenciaMayor(LocalDate fechaMayor) {
        return (root, cb) -> fechaMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("fechaVigencia"), fechaMayor);
    }

    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaIgual(LocalDate fecha) {
        return (root, cb) -> fecha == null
                ? cb.conjunction()
                : cb.equal(root.get("fechaCaducada"), fecha);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaMenor(LocalDate fechaMenor) {
        return (root, cb) -> fechaMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("fechaCaducada"), fechaMenor);
    }
    public static PredicateSpecification<PrecioRecursoEntity> fechaCaducadaMayor(LocalDate fechaMayor) {
        return (root, cb) -> fechaMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("fechaCaducada"), fechaMayor);
    }

    public static PredicateSpecification<PrecioRecursoEntity> precioIgual(Double precio){
        return (root, cb) -> precio == null
                ? cb.conjunction()
                : cb.equal(root.get("precio"), precio);
    }
    public static PredicateSpecification<PrecioRecursoEntity> precioMenor(Double precioMenor){
        return (root, cb) -> precioMenor == null
                ? cb.conjunction()
                : cb.lessThan(root.get("precio"), precioMenor);
    }
    public static PredicateSpecification<PrecioRecursoEntity> precioMayor(Double precioMayor){
        return (root, cb) -> precioMayor == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("precio"), precioMayor);
    }
}