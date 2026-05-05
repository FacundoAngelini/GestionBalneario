package com.Gestion.MiBalnearioGestion.Empleados;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadosRepository extends JpaRepository<EmpleadoEntity,Long> {

    boolean existePorIdPublico(UUID IdPublico);
    void borrarPorIdPublico(UUID IdPublico);
    Optional<EmpleadoEntity> buscarPorIdPublico(UUID IdPublico);
}