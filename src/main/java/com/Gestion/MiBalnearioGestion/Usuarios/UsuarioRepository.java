package com.Gestion.MiBalnearioGestion.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Long> {

    boolean existePorIdPublico(UUID idPublico);
    void borrarPorIDPublico(UUID idPublico);
    Optional<UsuarioEntity> findByUsuario(String nombre_de_usuario);


    Optional<UsuarioEntity>findByIdPublica (UUID idPublico);
}
