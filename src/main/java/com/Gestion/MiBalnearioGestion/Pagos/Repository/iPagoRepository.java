package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface iPagoRepository extends JpaRepository<PagoEntity, Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);

    Optional<PagoEntity> buscarPorIdPublica (UUID idPublico);
}
