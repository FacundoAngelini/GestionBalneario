package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface SombrillaRepositorio extends JpaRepository<SombrillaEntity, Long>, JpaSpecificationExecutor<SombrillaEntity> {
    Optional<SombrillaEntity> findByPublicId(UUID publicId);
    Optional<SombrillaEntity> findByNumero(int numero);
}
