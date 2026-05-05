package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iTicketRepository extends JpaRepository<TicketEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<TicketEntity> buscarPorIdPublica (UUID idPublico);
}
