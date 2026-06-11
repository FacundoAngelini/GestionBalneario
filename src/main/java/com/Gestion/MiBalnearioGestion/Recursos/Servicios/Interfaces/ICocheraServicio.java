package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CocheraRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CocheraResponseDTO;

import java.util.List;
import java.util.UUID;


public interface ICocheraServicio {
    CocheraResponseDTO crearCochera(CocheraRequestDTO dto);
    CocheraResponseDTO actualizarCochera(UUID id, CocheraRequestDTO dto);
    CocheraResponseDTO buscarPorId(UUID id);
    List<CocheraResponseDTO> buscarTodos(Integer numeroCochera, Integer numeroMayor, Integer numeroMenor);
    void desactivarCochera(UUID id);
}