package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PrecioRecursoRepositorio extends JpaRepository<PrecioRecursoEntity, Long>, JpaSpecificationExecutor<PrecioRecursoEntity> {
    Optional<PrecioRecursoEntity> findByPublicId(UUID publicId);
    List<PrecioRecursoEntity> findByRecursoPublicId(UUID recursoPublicId);
}
