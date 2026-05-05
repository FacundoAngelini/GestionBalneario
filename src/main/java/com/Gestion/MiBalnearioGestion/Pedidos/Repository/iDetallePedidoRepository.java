package com.Gestion.MiBalnearioGestion.Pedidos.Repository;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iDetallePedidoRepository extends JpaRepository<DetallePedidoEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<DetallePedidoEntity> buscarPorIdPublica (UUID idPublico);
}
