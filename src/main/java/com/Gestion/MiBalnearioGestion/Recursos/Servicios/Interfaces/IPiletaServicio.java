package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PiletaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PiletaResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IPiletaServicio {
    PiletaResponseDTO crearPileta(PiletaRequestDTO dto);
    PiletaResponseDTO actualizarPileta(UUID id, PiletaRequestDTO dto);
    PiletaResponseDTO buscarPorId(UUID id);
    List<PiletaResponseDTO> buscarTodos(Boolean esClimatizada, Integer tamanio);
    void desactivarPileta(UUID id);
}