package com.Gestion.MiBalnearioGestion.Reservas;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Server
public interface ReservaRepository extends JpaRepository<RecursoEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<RecursoEntity> buscarPorIdPublica (UUID idPublico);
}
