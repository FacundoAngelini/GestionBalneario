package com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.ResetearContraseniaTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IResetearContraseniaTokenRepository extends JpaRepository<ResetearContraseniaTokenEntity, Long> {

    Optional<ResetearContraseniaTokenEntity> findByToken(String token);
    // limpia tokens viejos
    void deleteByCredencial(CredencialEntity credencial);
}