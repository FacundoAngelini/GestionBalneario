package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SectorRepositorio extends JpaRepository<SectorEntity, Long> {
    Optional<SectorEntity> findByPublicId(UUID idPublica);
}
