package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;

import java.util.List;
import java.util.UUID;

public interface ICocheraServicio {
    List<CocheraDTO> listarCocheras(Integer cocheraIgual,
                                    Integer cocheraMenor,
                                    Integer cocheraMayor);
    CocheraDTO buscarCochera(UUID id);
    CocheraDTO actualizarCochera(UUID id, CocheraDTO dto);
    CocheraDTO crearCochera(CocheraDTO dto);
}
