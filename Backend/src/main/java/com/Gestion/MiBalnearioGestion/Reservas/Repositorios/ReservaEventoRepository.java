package com.Gestion.MiBalnearioGestion.Reservas.Repositorios;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservaEventoRepository extends JpaRepository<ReservaEventoEntity, Long> {
    List<ReservaEventoEntity> findByReservaPublicIdOrderByFechaAscIdAsc(UUID reservaPublicId);
}