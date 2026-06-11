package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.SombrillaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.SombrillaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;

import java.util.List;
import java.util.UUID;

public interface ISombrillaServicio {
    SombrillaResponseDTO crearSombrilla(SombrillaRequestDTO dto);
    SombrillaResponseDTO actualizarSombrilla(UUID id, SombrillaRequestDTO dto);
    SombrillaResponseDTO buscarPorId(UUID id);
    List<SombrillaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                           EtamanioSombrilla etamano);
    void desactivarSombrilla(UUID id);
}