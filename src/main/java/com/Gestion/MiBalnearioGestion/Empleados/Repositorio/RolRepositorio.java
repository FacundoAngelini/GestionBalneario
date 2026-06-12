package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolRepositorio extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByPublicId(UUID publicId);
}
