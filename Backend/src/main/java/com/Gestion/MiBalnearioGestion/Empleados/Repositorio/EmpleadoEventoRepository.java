package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmpleadoEventoRepository extends JpaRepository<EmpleadoEventoEntity, Long> {
    List<EmpleadoEventoEntity> findByEmpleadoPublicIdOrderByFechaAscIdAsc(UUID empleadoPublicId);
}