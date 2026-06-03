package com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio;

import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.Optional;

public interface RolesRepositorio extends JpaRepository<RolesEntity,Long> {
    Optional<RolesEntity> findByRole(Roles roles);
}
