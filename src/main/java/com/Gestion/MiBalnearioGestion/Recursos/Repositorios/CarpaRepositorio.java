package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import org.apache.el.parser.JJTELParserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CarpaRepositorio extends JpaRepository<CarpaEntity, Long>, JpaSpecificationExecutor<CarpaEntity> {
    Optional<CarpaEntity> findByPublicId(UUID id);
    Optional<CarpaEntity> findByNumero(int numero);
}
