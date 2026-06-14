package com.Gestion.MiBalnearioGestion.Productos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity,Long> {

    boolean existsByPublicId(UUID publicId);
    Optional<ProductoEntity> findByPublicId(UUID publicId);
    List<ProductoEntity> findByProductoDisponible(Boolean ProductoDisponible);
    List<ProductoEntity>findByNombreAndProductoDisponible(String nombre,boolean productoDisponible);
}
