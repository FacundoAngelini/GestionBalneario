package com.Gestion.MiBalnearioGestion.Productos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity,Long>, JpaSpecificationExecutor<ProductoEntity> {

    boolean existsByPublicId(UUID publicId);
    Optional<ProductoEntity> findByPublicId(UUID publicId);
    Optional<ProductoEntity> findByNombre(String nombre);
    List<ProductoEntity> findByProductoDisponible(Boolean ProductoDisponible);
    List<ProductoEntity> findByProductoDisponibleTrue();
    List<ProductoEntity>findByNombreAndProductoDisponible(String nombre,boolean productoDisponible);
}
