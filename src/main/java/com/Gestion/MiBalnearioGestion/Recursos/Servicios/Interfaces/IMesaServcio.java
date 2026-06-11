package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.MesaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.MesaResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IMesaServcio {
    MesaResponseDTO crearMesa(MesaRequestDTO dto);
    MesaResponseDTO actualizarMesa(UUID id, MesaRequestDTO dto);
    MesaResponseDTO buscarPorId(UUID id);
    List<MesaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                      Integer capacidadIgual, Integer capacidadMayor, Integer capacidadMenor);
    void desactivarMesa(UUID id);
}
