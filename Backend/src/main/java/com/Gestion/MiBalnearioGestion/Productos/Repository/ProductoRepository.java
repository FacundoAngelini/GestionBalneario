package com.Gestion.MiBalnearioGestion.Productos.Repository;

import com.Gestion.MiBalnearioGestion.Productos.Entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity,Long>, JpaSpecificationExecutor<ProductoEntity> {

    Optional<ProductoEntity> findByPublicId(UUID publicId);
    Optional<ProductoEntity> findByNombre(String nombre);
    List<ProductoEntity> findByProductoDisponibleTrue();
}
