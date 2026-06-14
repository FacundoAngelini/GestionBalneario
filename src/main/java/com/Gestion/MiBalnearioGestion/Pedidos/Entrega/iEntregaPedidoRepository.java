package com.Gestion.MiBalnearioGestion.Pedidos.Entrega;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iEntregaPedidoRepository extends JpaRepository<EntregaEntity, Long> {
    Optional<EntregaEntity> findByPublicId(UUID publicId);
    Optional<EntregaEntity> findByPedidoReservaPublicId(UUID pedidoReservaPublicId);
}
