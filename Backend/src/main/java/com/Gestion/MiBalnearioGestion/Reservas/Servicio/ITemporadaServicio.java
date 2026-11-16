package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;

import java.time.LocalDate;

public interface ITemporadaServicio {
    ConfiguracionTemporadaEntity guardar(LocalDate inicio, LocalDate fin);
}
