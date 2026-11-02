package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoUpdateDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioNombreUsuarioRequest;

import java.util.List;
import java.util.UUID;

public interface IEmpleadoService {

    EmpleadoResponseDTO crearEmpleado (EmpleadoDTO dtoEmpleado);
    void borrarEmpleado(UUID IDpublico);
    List<EmpleadoResponseDTO> listarEmpleados(  Integer dniIgual, Integer dniContiene,
                                        String nombreIgual, String nombreContiene,
                                        String apellidoIgual, String apellidoContiene,
                                        String telefonoIgual, String telefonoContiene,
                                        String cuitIgual, String cuitContiene,
                                        Double sueldoIgual, Double sueldoMenor, Double sueldoMayor,
                                        String sectorIgual, String sectorContiene,
                                        String rolIgual, String rolContiene,
                                        String calleIgual, String calleContiene,
                                        Integer numeroIgual, Integer numeroContiene,
                                        String ciudadIgual, String ciudadContiene,
                                        String provinciaIgual, String provinciaContiene,
                                        EEstadoEmpleado estadoIgual) ;
    EmpleadoResponseDTO buscarPorIDpublico(UUID IDpublico);
    EmpleadoResponseDTO reactivarEmpleado(UUID publicId);
    void cambiarContraseniaEmpleado(UUID publicId, CambioContraseniaRequest request);
    void cambiarNombreUsuarioEmpleado(UUID publicId, CambioNombreUsuarioRequest request);
    EmpleadoResponseDTO actualizarEmpleado(UUID IDpublico, EmpleadoUpdateDTO dto);
}
