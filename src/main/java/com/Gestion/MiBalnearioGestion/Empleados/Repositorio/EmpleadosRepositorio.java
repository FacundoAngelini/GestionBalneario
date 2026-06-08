package com.Gestion.MiBalnearioGestion.Empleados.Repositorio;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadosRepositorio extends JpaRepository<EmpleadoEntity,Long>, JpaSpecificationExecutor<EmpleadoEntity> {

    Optional<EmpleadoEntity> findByNombre(String nombre);
    Optional<EmpleadoEntity> findByPublicId(UUID IdPublico);
    Optional<EmpleadoEntity> findByDni(int dni);
    Optional<EmpleadoEntity> findByEmail(String email);
    Optional<EmpleadoEntity> findByCuit(String cuit);

}