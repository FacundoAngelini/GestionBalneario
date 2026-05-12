package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;

import java.util.List;
import java.util.UUID;

public interface IEmpleadoServicio {

    EmpleadoDTO crearEmpleado (EmpleadoDTO dtoEmpleado);
    void borrarEmpleado(UUID IDpublico);
    EmpleadoDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto);
    List<EmpleadoDTO> buscarTodos();
    EmpleadoDTO buscarPorIDpublico(UUID IDpublico);
}
