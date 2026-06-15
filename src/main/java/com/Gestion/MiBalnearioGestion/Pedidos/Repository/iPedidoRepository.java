package com.Gestion.MiBalnearioGestion.Pedidos.Repository;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface iPedidoRepository extends JpaRepository<PedidoEntity, Long>, JpaSpecificationExecutor<PedidoEntity> {
    Optional<PedidoEntity> findByPublicId(UUID publicId);
    List<PedidoEntity> findByEstadoPedidoAndFechaCreacionBefore(EEstadoPedido estado, LocalDateTime limite);
    @Query("SELECT p FROM PedidoEntity p WHERE p.estadoPedido = :estado AND p.fechaCreacion < :limite")
    List<PedidoEntity> findVencidos(@Param("estado") EEstadoPedido estado,
                                    @Param("limite") LocalDateTime limite);
}
