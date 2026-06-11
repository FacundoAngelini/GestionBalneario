package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface SectorRepositorio extends JpaRepository<SectorEntity, Long> {
    Optional<SectorEntity> findByPublicId(UUID idPublica);
    Optional<SectorEntity> findByNombre(String nombre);
    Boolean existsByPublicId(UUID id);
}
