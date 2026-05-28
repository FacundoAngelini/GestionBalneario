package com.Gestion.MiBalnearioGestion.Reservas;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<RecursoEntity, Long> {

}
