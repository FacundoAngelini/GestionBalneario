package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import jakarta.persistence.LockModeType;
import org.apache.logging.log4j.simple.internal.SimpleProvider;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecursoRepositorio extends JpaRepository<RecursoEntity, Long>, JpaSpecificationExecutor<RecursoEntity> {
    Optional<RecursoEntity> findByPublicId(UUID publicId);

    long countByEsReservableTrue();

    @Modifying
    @Query("UPDATE RecursoEntity r SET r.esReservable = false")
    void desactivarTodoElInventario();

    @Query("SELECT r FROM RecursoEntity r LEFT JOIN FETCH r.precioRecurso WHERE r.publicId = :publicId")
    Optional<RecursoEntity> findByPublicIdWithPrecios(@Param("publicId") UUID publicId);

    @Query("SELECT r FROM RecursoEntity r WHERE r.esReservable = true AND r.id NOT IN (" +
            "    SELECT DISTINCT rec.id FROM ReservaEntity res " +
            "    JOIN res.recursos rec " +
            "    WHERE res.estadoReserva IN :estadosConflictivos " +
            "    AND (:fechaInicio <= res.fechaFin AND :fechaFin >= res.fechaInicio)" + ")")
    List<RecursoEntity> encontrarDisponiblesEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin,
                                                    @Param("estadosConflictivos") List<EReservaEstado> estadosConflictivos);

    // Bloquea las filas hasta que termine la transacción. El ORDER BY es el mismo para todos: evita bloqueos cruzados.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RecursoEntity r WHERE r.publicId IN :ids ORDER BY r.id")
    List<RecursoEntity> bloquearPorPublicIds(@Param("ids") Collection<UUID> ids);
}

