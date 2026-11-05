package com.Gestion.MiBalnearioGestion.Clientes.Repository;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClienteEventoRepository extends JpaRepository<ClienteEventoEntity, Long> {
    List<ClienteEventoEntity> findByClientePublicIdOrderByFechaAscIdAsc(UUID clientePublicId);
}