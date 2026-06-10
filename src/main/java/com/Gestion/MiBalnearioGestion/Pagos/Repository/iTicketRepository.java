package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iTicketRepository extends JpaRepository<TicketEntity, Long> {

    Optional<TicketEntity>findByPublicId(UUID publicId);
    boolean existsByPublicId(UUID publicId);
}
