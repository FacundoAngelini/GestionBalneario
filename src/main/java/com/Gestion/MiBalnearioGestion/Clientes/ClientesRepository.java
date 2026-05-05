package com.Gestion.MiBalnearioGestion.Clientes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientesRepository extends JpaRepository<ClienteEntity,Long> {

    boolean existePorIdPublico(UUID IdPublico);
    void borrarPorIdPublico(UUID IdPublico);
    Optional<ClienteEntity> buscarPorIdPublico(UUID IdPublico);
}
