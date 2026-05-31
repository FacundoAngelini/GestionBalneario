package com.Gestion.MiBalnearioGestion.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Long> {


    Optional<UsuarioEntity> findByNombreUsuario(String nombre_de_usuario);
    //Optional<UsuarioEntity> findByNombreUsuario(String nombre_de_usuario);
    Optional<UsuarioEntity>findByPublicId (UUID idPublico);
}
