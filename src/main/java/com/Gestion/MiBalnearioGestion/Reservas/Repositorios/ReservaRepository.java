package com.Gestion.MiBalnearioGestion.Reservas.Repositorios;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
 Optional<ReservaEntity>findByPublicId(UUID publicId);

    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r JOIN r.recursos rec " +
            "WHERE rec.publicId = :recursoId " +
            "AND r.estadoReserva IN :estados " +
            "AND (:fechaInicio < r.fechaFin AND :fechaFin > r.fechaInicio)")
    boolean isRecursoOcupadoEnFechas(
            @Param("recursoId") UUID recursoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estados") List<EReservaEstado> estados
    );
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva = :estado AND r.fechaInicio <= :fechaLimite")
    List<ReservaEntity> findReservasExpiradas(@Param("estado") EReservaEstado estado, @Param("fechaLimite") LocalDate fechaLimite);

}

