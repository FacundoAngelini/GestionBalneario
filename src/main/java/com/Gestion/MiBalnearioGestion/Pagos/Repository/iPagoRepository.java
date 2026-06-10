package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iPagoRepository extends JpaRepository<PagoEntity, Long> {
    Optional<PagoEntity>findByPublicId(UUID publicId);
    boolean existsByPublicId(UUID publicId);
    @Query("SELECT p FROM PagoReservaEntity p WHERE p.reserva.publicId = :reservaPublicId")
    Optional<PagoEntity> findByReservaPublicId(@Param("reservaPublicId") UUID reservaPublicId);
}
