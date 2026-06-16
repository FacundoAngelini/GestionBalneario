package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    Optional<TicketEntity>findByPublicId(UUID publicId);
    boolean existsByPublicId(UUID publicId);
    boolean existsByPagoEntityId(Long pagoId);
}
