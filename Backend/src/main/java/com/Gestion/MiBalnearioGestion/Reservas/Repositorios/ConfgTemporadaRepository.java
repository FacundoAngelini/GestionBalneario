package com.Gestion.MiBalnearioGestion.Reservas.Repositorios;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfgTemporadaRepository extends JpaRepository<ConfiguracionTemporadaEntity, Long> {
    Optional<ConfiguracionTemporadaEntity> findFirstByOrderByIdDesc();
}