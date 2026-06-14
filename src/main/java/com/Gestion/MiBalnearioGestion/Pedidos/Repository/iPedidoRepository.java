package com.Gestion.MiBalnearioGestion.Pedidos.Repository;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface iPedidoRepository extends JpaRepository<PedidoEntity, Long> {
    Optional<PedidoEntity> findByPublicId(UUID publicId);
    List<PedidoEntity> findByTipoPedido(ETipoPedido tipoPedido);
}
