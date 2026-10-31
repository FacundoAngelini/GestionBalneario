package com.Gestion.MiBalnearioGestion.Reservas.Repositorios;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long>, JpaSpecificationExecutor<ReservaEntity> {
 Optional<ReservaEntity>findByPublicId(UUID publicId);

    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r JOIN r.recursos rec " +
            "WHERE rec.publicId = :recursoId " +
            "AND r.estadoReserva IN :estados " +
            "AND (:fechaInicio <= r.fechaFin AND :fechaFin >= r.fechaInicio)")
    boolean isRecursoOcupadoEnFechas(
            @Param("recursoId") UUID recursoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estados") List<EReservaEstado> estados
    );
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva = :estado AND r.fechaInicio <= :fechaLimite")
    List<ReservaEntity> findReservasExpiradas(@Param("estado") EReservaEstado estado, @Param("fechaLimite") LocalDate fechaLimite);
    List<ReservaEntity> findByEstadoReservaAndFechaCreacionBefore(EReservaEstado estado, LocalDateTime limite);
    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r JOIN r.recursos rec " +
            "WHERE r.cliente.publicId = :clienteId " +
            "AND r.estadoReserva IN :estados " +
            "AND TYPE(rec) IN (CarpaEntity, SombrillaEntity) " +
            "AND r.fechaInicio <= :inicio AND r.fechaFin >= :fin")
    boolean clienteTieneAlojamientoQueCubre(
            @Param("clienteId") UUID clienteId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin,
            @Param("estados") List<EReservaEstado> estados);

    /** Igual que clienteTieneAlojamientoQueCubre, pero ignorando una reserva (la que se está cancelando). */
   @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r JOIN r.recursos rec " +
           "WHERE r.cliente.publicId = :clienteId " +
           "AND r.estadoReserva IN :estados " +
           "AND r.publicId <> :excluirReservaId " +
           "AND TYPE(rec) IN (CarpaEntity, SombrillaEntity) " +
           "AND r.fechaInicio <= :inicio AND r.fechaFin >= :fin")
   boolean clienteTieneAlojamientoQueCubreExcluyendo(
           @Param("clienteId") UUID clienteId,
           @Param("inicio") LocalDate inicio,
           @Param("fin") LocalDate fin,
           @Param("estados") List<EReservaEstado> estados,
           @Param("excluirReservaId") UUID excluirReservaId);

           /** Reservas con cochera del cliente que se pisan con un rango de fechas (sin contar una reserva). */
           @Query("SELECT DISTINCT r FROM ReservaEntity r JOIN r.recursos rec " +
                   "WHERE r.cliente.publicId = :clienteId " +
                   "AND r.estadoReserva IN :estados " +
                   "AND r.publicId <> :reservaId " +
                   "AND TYPE(rec) = CocheraEntity " +
                   "AND r.fechaInicio <= :fin AND r.fechaFin >= :inicio")
           List<ReservaEntity> cocherasDelClienteEnRango(
            @Param("clienteId") UUID clienteId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin,
            @Param("estados") List<EReservaEstado> estados,
            @Param("reservaId") UUID reservaId);
}

