package com.Gestion.MiBalnearioGestion.Reservas.Servicio;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ConfgTemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
    @Service
    @RequiredArgsConstructor
    public class TemporadaServicio implements ITemporadaServicio{

        private final ConfgTemporadaRepository configRepository;

        /** Crea una nueva configuración. La vigente es siempre la última, así que se conserva el historial. */
        @Transactional
        public ConfiguracionTemporadaEntity guardar(LocalDate inicio, LocalDate fin) {
            if (fin.isBefore(inicio)) {
                throw new DatosInvalidoException("El fin de temporada no puede ser anterior al inicio", "ConfiguracionTemporadaEntity");
            }
            return configRepository.save(ConfiguracionTemporadaEntity.builder()
                    .inicioTemporada(inicio)
                    .fin_temporada(fin)
                    .build());
        }
    }
