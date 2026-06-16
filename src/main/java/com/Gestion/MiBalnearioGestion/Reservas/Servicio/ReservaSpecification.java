package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;
import java.util.UUID;

public class ReservaSpecification {

    public static PredicateSpecification<ReservaEntity> estadoIgual(EReservaEstado estado) {
        return (root, cb) -> estado == null
                ? cb.conjunction()
                : cb.equal(root.get("estadoReserva"), estado);
    }

    public static PredicateSpecification<ReservaEntity> fechaInicioDesde(LocalDate fechaDesde) {
        return (root, cb) -> fechaDesde == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaInicio"), fechaDesde);
    }

    public static PredicateSpecification<ReservaEntity> fechaFinHasta(LocalDate fechaHasta) {
        return (root, cb) -> fechaHasta == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaFin"), fechaHasta);
    }

    public static PredicateSpecification<ReservaEntity> clientePublicIdIgual(UUID clientePublicId) {
        return (root, cb) -> clientePublicId == null
                ? cb.conjunction()
                : cb.equal(root.get("cliente").get("publicId"), clientePublicId);
    }
}