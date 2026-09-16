package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;
import java.util.UUID;

public class TicketSpecification {
    public static PredicateSpecification<TicketEntity> fechaDesde(LocalDateTime desde) {
        return (root, cb) -> desde == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("fechaTicket"), desde);
    }

    public static PredicateSpecification<TicketEntity> fechaHasta(LocalDateTime hasta) {
        return (root, cb) -> hasta == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("fechaTicket"), hasta);
    }

    public static PredicateSpecification<TicketEntity> empleadoIgual(UUID empleadoPublicId) {
        return (root, cb) -> empleadoPublicId == null
                ? cb.conjunction()
                : cb.equal(root.get("empleado").get("publicId"), empleadoPublicId);
    }
}
