package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface MesaRepositorio extends JpaRepository<MesaEntity, Long>, JpaSpecificationExecutor<MesaEntity> {
    Optional<MesaEntity> findByPublicId(UUID publicId);
    Optional<MesaEntity> findByNumero(int numero);
}
