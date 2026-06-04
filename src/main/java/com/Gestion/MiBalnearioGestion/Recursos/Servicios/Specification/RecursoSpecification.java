package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

public class RecursoSpecification {
    public static PredicateSpecification<RecursoEntity> nombreIgual(String nombre){
        return (root,cb)->nombre==null|| nombre.isBlank()
                ?cb.conjunction()
                : cb.equal(root.get("nombre"), nombre);
    }

    public static PredicateSpecification<RecursoEntity> nombreContiene(String nombre){
        return (root,cb)->nombre==null|| nombre.isBlank()
                ?cb.conjunction()
                : cb.like(root.get("nombre"),"%" + nombre + "%");
    }

    public static PredicateSpecification<RecursoEntity> reservableVerdad(Boolean esReservable){
        return (root,cb)->cb.equal(root.get("true"), esReservable);
    }
}
