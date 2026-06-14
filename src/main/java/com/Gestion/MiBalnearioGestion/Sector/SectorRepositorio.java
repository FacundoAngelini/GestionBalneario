package com.Gestion.MiBalnearioGestion.Sector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface SectorRepositorio extends JpaRepository<SectorEntity, Long> {
    Optional<SectorEntity> findByPublicId(UUID idPublica);
    Optional<SectorEntity> findByNombre(String nombre);
    Optional<SectorEntity> findByNombreIgnoreCase(String nombre);
}
