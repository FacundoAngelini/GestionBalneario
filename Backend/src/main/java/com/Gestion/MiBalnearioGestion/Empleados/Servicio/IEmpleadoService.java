package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.*;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;

import java.util.List;
import java.util.UUID;

public interface IEmpleadoService {
    EmpleadoFichaDTO crearEmpleado(EmpleadoDTO dto);
    List<EmpleadoFichaDTO> listarEmpleados(EEstadoEmpleado estado);
    EmpleadoFichaDTO buscarPorIDpublico(UUID id);
    EmpleadoFichaDTO actualizarEmpleado(UUID id, EmpleadoUpdateDTO dto);
    EmpleadoFichaDTO cambiarEstado(UUID id, CambioEstadoEmpleadoDTO dto);
    EmpleadoResponseDTO actualizarMiFicha(String nombreUsuario, EmpleadoPerfilUpdateDTO dto);
    List<EventoEmpleadoDTO> historial(UUID id);
}