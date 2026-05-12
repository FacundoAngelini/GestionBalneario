package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Empleados.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadosRepository extends JpaRepository<EmpleadoEntity,Long> {

    boolean existePorIdPublico(UUID IdPublico);
    void borrarPorIdPublico(UUID IdPublico);
    Optional<EmpleadoEntity> findByIdPublico(UUID IdPublico);
    Optional<EmpleadoEntity> findByDni(int dni);
    Optional<EmpleadoEntity> findByEmail(String email);
    Optional<EmpleadoEntity> findByCuit(String cuit);
    Optional<EmpleadoEntity> findByEstado(EEstadoEmpleado estado);
    Optional<EmpleadoEntity> findByRol(RolEntity rol);
    Optional<EmpleadoEntity> findBySector(SectorEntity sector);
    Optional<EmpleadoEntity> findByNombre(String nombre);
    Optional<EmpleadoEntity> findByApellido(String apellido);
    Optional<EmpleadoEntity> findByNombreAndApellido(String nombre, String apellido);
    Optional<EmpleadoEntity> findBySueldo(double sueldo);
    void deleteByIdPublico(UUID iDpublico);
}