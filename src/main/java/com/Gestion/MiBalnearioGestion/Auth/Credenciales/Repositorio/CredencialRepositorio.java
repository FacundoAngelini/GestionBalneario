package com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialRepositorio extends JpaRepository<CredencialEntity, Long> {
    Optional<CredencialEntity> findByNombreUsuario(String nombreUsuario);
    boolean existsByNombreUsuario(String nombreUsuario);
    Optional<CredencialEntity> findByUsuario(UsuarioEntity usuario);
}
