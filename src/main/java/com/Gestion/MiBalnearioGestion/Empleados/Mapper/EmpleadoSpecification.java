package com.Gestion.MiBalnearioGestion.Empleados.Mapper;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class EmpleadoSpecification {

    public static PredicateSpecification<EmpleadoEntity> nameEquals(String nombre){
        return (root, cb)-> nombre == null || nombre.isBlank()
                ? cb.conjunction()
                : cb.equal(cb.lower(root.get("name")),nombre.toLowerCase());
    }
}
