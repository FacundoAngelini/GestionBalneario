package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ConfgTemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TemporadaValidator {

    private final ConfgTemporadaRepository configRepository;

    public void validarFechasEnTemporada(LocalDate inicio, LocalDate fin) {
        ConfiguracionTemporadaEntity config = configRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new DatosInvalidoException("No hay ninguna configuración de temporada dada de alta en el sistema","ConfiguracionEntity"));

        if (fin.isBefore(inicio)) {
            throw new DatosInvalidoException("La fecha de fin no puede ser anterior a la de inicio.","ConfiguracionEntity");
        }

        if (inicio.isBefore(config.getInicioTemporada())) {
            throw new DatosInvalidoException("Fuera de temporada. El balneario abre sus puertas el " + config.getInicioTemporada(),"ConfiguracionEntity");
        }

        if (fin.isAfter(config.getFin_temporada())) {
            throw new DatosInvalidoException("Fuera de temporada. La temporada finaliza estrictamente el " + config.getFin_temporada(),"ConfiguracionEntity");
        }
    }
}