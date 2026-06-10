package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PiletaRepositorio extends JpaRepository<PiletaEntity, Long>, JpaSpecificationExecutor<PiletaEntity> {
    Optional<PiletaEntity> findByPublicId(UUID id);
}
