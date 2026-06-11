package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.RecursoRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.RecursoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IRecursoServicio {

    // Lectura
    RecursoResponseDTO buscarPorPublicId(UUID publicId);
    List<RecursoResponseDTO> buscarTodos(String nombreIgual, String nombreContiene, Boolean reservableVerdad);
    List<RecursoResponseDTO> buscarPorSector(UUID sectorPublicId);

    // Activación / desactivación individual
    void desactivarRecurso(UUID publicId);
    void activarRecurso(UUID publicId);

    // Operaciones masivas (las tuyas, intactas)
    void desactivarTodoElInventario();
    void borrarTodoElInventario();
    void borrarRecurso(UUID publicId);
}
