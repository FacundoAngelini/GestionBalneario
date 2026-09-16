package com.Gestion.MiBalnearioGestion.Pedidos.Repository;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDetallePedidoRepository extends JpaRepository<DetallePedidoEntity, Long> {
    Optional<DetallePedidoEntity> findByPublicId(UUID id);


}
