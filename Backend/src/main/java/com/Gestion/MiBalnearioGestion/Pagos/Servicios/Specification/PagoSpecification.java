package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Specification;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;

public class PagoSpecification {

    public static PredicateSpecification<PagoEntity> estadoIgual(EestadoPago estado) {
        return (root, cb) -> estado == null
                ? cb.conjunction()
                : cb.equal(root.get("eestadoPago"), estado);
    }

    public static PredicateSpecification<PagoEntity> metodoPagoIgual(MetodoPago metodo) {
        return (root, cb) -> metodo == null
                ? cb.conjunction()
                : cb.equal(root.get("metodoPago"), metodo);
    }

    public static PredicateSpecification<PagoEntity> montoIgual(Double monto) {
        return (root, cb) -> monto == null
                ? cb.conjunction()
                : cb.equal(root.get("monto"), monto);
    }

    public static PredicateSpecification<PagoEntity> montoMayorOIgual(Double montoMinimo) {
        return (root, cb) -> montoMinimo == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("monto"), montoMinimo);
    }

    public static PredicateSpecification<PagoEntity> montoMenorOIgual(Double montoMaximo) {
        return (root, cb) -> montoMaximo == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("monto"), montoMaximo);
    }

    public static PredicateSpecification<PagoEntity> fechaPagoIgual(LocalDate fecha) {
        return (root, cb) -> fecha == null
                ? cb.conjunction()
                : cb.equal(root.get("fechaPago"), fecha);
    }

    public static PredicateSpecification<PagoEntity> fechaPagoDesde(LocalDate fechaDesde) {
        return (root, cb) -> fechaDesde == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaPago"), fechaDesde);
    }

    public static PredicateSpecification<PagoEntity> fechaPagoHasta(LocalDate fechaHasta) {
        return (root, cb) -> fechaHasta == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaPago"), fechaHasta);
    }
}