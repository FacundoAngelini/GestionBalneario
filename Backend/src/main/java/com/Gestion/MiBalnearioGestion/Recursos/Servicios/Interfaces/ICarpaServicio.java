package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;

import java.util.List;
import java.util.UUID;

public interface ICarpaServicio {
    CarpaDTO crearCarpa(CarpaDTO carpa);
    CarpaDTO actualizarCarpa(CarpaDTO carpa, UUID id);
    CarpaDTO buscarPorId(UUID id);
    List<CarpaDTO> buscarTodos(Integer numero,
                               Integer numeroMayor,
                               Integer numeroMenor,
                               Integer pasilloIgual,
                               Integer pasilloMayor,
                               Integer pasilloMenor,
                               Integer capacidadIgual
    );
}
