package com.Gestion.MiBalnearioGestion.Pedidos.Servicios;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;

public class PedidoSpecification {

    public static PredicateSpecification<PedidoEntity> tipoIgual(ETipoPedido tipoPedido) {
        return (root, cb) -> tipoPedido == null
                ? cb.conjunction()
                : cb.equal(root.get("tipoPedido"), tipoPedido);
    }

    public static PredicateSpecification<PedidoEntity> estadoIgual(EEstadoPedido estadoPedido) {
        return (root, cb) -> estadoPedido == null
                ? cb.conjunction()
                : cb.equal(root.get("estadoPedido"), estadoPedido);
    }
    public static PredicateSpecification<PedidoEntity> fechaIgual(LocalDate fecha) {
        return (root, cb) -> fecha == null
                ? cb.conjunction()
                : cb.equal(root.get("fechaPedido"), fecha);
    }
}