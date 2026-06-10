package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CocheraRepositorio extends JpaRepository<CocheraEntity, Long>, JpaSpecificationExecutor<CocheraEntity> {
    Optional<CocheraEntity> findByPublicId(UUID Id);
    Optional<CocheraEntity> findByNumeroCochera(int numero);
}
