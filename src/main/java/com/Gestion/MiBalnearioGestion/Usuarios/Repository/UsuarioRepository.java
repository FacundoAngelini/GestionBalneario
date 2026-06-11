package com.Gestion.MiBalnearioGestion.Usuarios.Repository;

import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Long>, JpaSpecificationExecutor<UsuarioEntity> {

    Optional<UsuarioEntity>findByPublicId (UUID idPublico);
}
