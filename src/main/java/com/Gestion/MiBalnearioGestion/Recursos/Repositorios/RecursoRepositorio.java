package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.apache.logging.log4j.simple.internal.SimpleProvider;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecursoRepositorio extends JpaRepository<RecursoEntity, Long>, JpaSpecificationExecutor<RecursoEntity> {
    Optional<RecursoEntity> findByPublicId(UUID publicId);

    long countByEsReservableTrue(); //cuenta la cantidad de reservables

    @Modifying
    @Query("UPDATE RecursoEntity r SET r.esReservable = false")
    void desactivarTodoElInventario();

}

