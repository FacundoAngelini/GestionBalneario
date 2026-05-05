package com.Gestion.MiBalnearioGestion.Pedidos.Repository;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.EntregaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iEntregaPedidoRepository extends JpaRepository<EntregaEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<EntregaEntity> buscarPorIdPublica (UUID idPublico);
}
