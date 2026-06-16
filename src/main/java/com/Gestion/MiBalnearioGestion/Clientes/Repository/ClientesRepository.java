package com.Gestion.MiBalnearioGestion.Clientes.Repository;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientesRepository extends JpaRepository<ClienteEntity,Long>, JpaSpecificationExecutor<ClienteEntity> {

    Optional<ClienteEntity> findByPublicId(UUID IdPublico);

    Optional<ClienteEntity> findByDni(Integer dni);

    Optional<ClienteEntity> findByEmail(String email);
}

