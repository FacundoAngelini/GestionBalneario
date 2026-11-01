package com.Gestion.MiBalnearioGestion.Pagos.Repository;

import com.Gestion.MiBalnearioGestion.Pagos.Entity.ConfiguracionPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IConfiguracionPagoRepository extends JpaRepository<ConfiguracionPagoEntity, Long> {}