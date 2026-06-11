package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CarpaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CarpaResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ICarpaServicio {
    CarpaResponseDTO crearCarpa(CarpaRequestDTO dto);
    CarpaResponseDTO actualizarCarpa(UUID id, CarpaRequestDTO dto);
    CarpaResponseDTO buscarPorId(UUID id);
    List<CarpaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                       Integer pasilloIgual, Integer pasilloMayor, Integer pasilloMenor,
                                       Integer capacidadIgual);
    void desactivarCarpa(UUID id);
}