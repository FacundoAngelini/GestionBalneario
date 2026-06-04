package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CanchaRepositorio extends JpaRepository<CanchaEntity, Long>, JpaSpecificationExecutor<CanchaEntity> {
    Optional<CanchaEntity> findByPublicId(UUID Id);
}
