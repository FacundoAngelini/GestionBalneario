package com.Gestion.MiBalnearioGestion.Recursos.Repositorios;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecursoRepositorio extends JpaRepository<RecursoEntity, Long> {

}

