package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRecursoServicio {
    RecursoDTO buscarPorPublicId(UUID publicId);
    void desactivarRecurso(UUID publicId);
    void desactivarTodoElInventario();
    void borrarTodoElInventario();
    List<RecursoDTO> buscarTodos(String nombreIgual,
                                 String nombreContiene,
                                 Boolean reservableVerdad);
    void borrarRecurso(UUID IdPublico);
    List<RecursoDTO> listarDisponiblesParaElCliente(LocalDate fechaInicio, LocalDate fechaFin);
}
