package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CanchaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CanchaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;

import java.util.List;
import java.util.UUID;


public interface ICanchaServicio {
    CanchaResponseDTO crearCancha(CanchaRequestDTO dto);
    CanchaResponseDTO actualizarCancha(UUID id, CanchaRequestDTO dto);
    CanchaResponseDTO buscarPorId(UUID id);
    List<CanchaResponseDTO> buscarTodos(ETipoCancha tipoCancha, Integer capacidadIgual,
                                        Integer capacidadMayor, Integer capacidadMenor,
                                        Boolean iluminacion);
    void desactivarCancha(UUID id);
}


