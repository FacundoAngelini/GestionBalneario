package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepositorio extends JpaRepository<SectorEntity, Long> {
}
