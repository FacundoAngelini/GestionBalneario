package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ECategoriaProdcuto;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class ProductoSpecification {

    public static PredicateSpecification<ProductoEntity> nombreContiene(String nombre) {
        return (root, cb) -> (nombre == null || nombre.isBlank())
                ? cb.conjunction() // Equivale a un "1=1" (no filtra nada)
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