package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepositorio extends JpaRepository<RolEntity, Long> {
}
