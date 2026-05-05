package com.Gestion.MiBalnearioGestion.Recursos;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecursoRepository extends JpaRepository<RecursoEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<RecursoEntity> buscarPorIdPublica (UUID idPublico);

}

