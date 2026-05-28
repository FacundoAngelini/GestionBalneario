package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;

import java.util.List;
import java.util.UUID;

public interface IEmpleadoService {

    EmpleadoDTO crearEmpleado (EmpleadoDTO dtoEmpleado);
    void borrarEmpleado(UUID IDpublico);
    EmpleadoDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto);
    List<EmpleadoDTO> buscarTodos(Integer dniIgual,
                                  Integer dniContiene,
                                  String nombreIgual,
                                  String nombreContiene,
                                  String apellidoIgual,
                                  String apellidoContiene,
                                  String telefonoIgual,
                                  String telefonoContiene,
                                  String cuitIgual,
                                  String cuitContiene,
                                  Double sueldoIgual,
                                  Double sueldoMenor,
                                  Double sueldoMayor,
                                  String sectorIgual,
                                  String sectorContiene,
                                  String rolIgual,
                                  String rolContiene,
                                  String calleIgual,
                                  String calleContiene,
                                  Integer numeroIgual,
                                  Integer numeroContiene,
                                  String ciudadIgual,
                                  String ciudadContiene,
                                  String privinciaIgual,
                                  String provinciaContiene,
                                  EEstadoEmpleado estadoIgual,
                                  EEstadoEmpleado estadoActivo,
                                  EEstadoEmpleado estadoInactivo);
    EmpleadoDTO buscarPorIDpublico(UUID IDpublico);
}
