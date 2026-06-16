package com.Gestion.MiBalnearioGestion.Productos.Service;

import com.Gestion.MiBalnearioGestion.Productos.Entity.ECategoriaProdcuto;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ProductoEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class ProductoSpecification {

    public static PredicateSpecification<ProductoEntity> nombreContiene(String nombre) {
        return (root, cb) -> (nombre == null || nombre.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static PredicateSpecification<ProductoEntity> categoriaIgual(ECategoriaProdcuto categoria) {
        return (root, cb) -> categoria == null
                ? cb.conjunction()
                : cb.equal(root.get("categoria"), categoria);
    }

    public static PredicateSpecification<ProductoEntity> disponibleIgual(Boolean disponible) {
        return (root, cb) -> disponible == null
                ? cb.conjunction()
                : cb.equal(root.get("productoDisponible"), disponible);
    }
}