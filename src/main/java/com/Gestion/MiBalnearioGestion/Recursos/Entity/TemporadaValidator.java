package com.Gestion.MiBalnearioGestion.Recursos.Entity;

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
        // 1. Buscamos la última configuración guardada por el administrador
        ConfiguracionTemporadaEntity config = configRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new IllegalStateException("No hay ninguna configuración de temporada dada de alta en el sistema."));

        // 2. Validar que el inicio no sea posterior al fin de la reserva
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }

        // 3. Validar el límite de inicio de temporada
        if (inicio.isBefore(config.getInicioTemporada())) {
            throw new IllegalArgumentException("Fuera de temporada. El balneario abre sus puertas el " + config.getInicioTemporada());
        }

        // 4. Validar el límite de fin de temporada
        if (fin.isAfter(config.getFin_temporada())) {
            throw new IllegalArgumentException("Fuera de temporada. La temporada finaliza estrictamente el " + config.getFin_temporada());
        }
    }
}