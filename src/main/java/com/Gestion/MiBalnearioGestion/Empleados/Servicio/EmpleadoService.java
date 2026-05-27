package com.Gestion.MiBalnearioGestion.Empleados.Servicio;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.ExEntidadExistente;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Servicio.EmpleadosRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpleadoService implements IEmpleadoService {
    private final EmpleadosRepository empleadosRepositorio;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoMapper empleadoMapper;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    @Override
    public EmpleadoDTO crearEmpleado (EmpleadoDTO dtoEmpleado){ // no deberia devolver una entity para el controller

        if (empleadosRepositorio.findByDni(dtoEmpleado.getDni()).isPresent()){
            throw new ExEntidadExistente("Ya existe un empleado con ese DNI", "EmpleadoEntity");
        }

        if (empleadosRepositorio.findByEmail(dtoEmpleado.getEmail()).isPresent()) {
            throw new ExEntidadExistente("Ya existe un empleado con ese email", "EmpleadoEntity");
        }

        if(empleadosRepositorio.findByCuit(dtoEmpleado.getCuit()).isPresent()){
            throw new ExEntidadExistente("Ya existe un empleado con ese cuit", "EmpleadoEntity");
        }

        UsuarioEntity usuario = usuarioMapper.convertToEntity(dtoEmpleado.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dtoEmpleado, EmpleadoEntity.class);
        empleado.setUsuario(usuarioGuardado);
        empleado.setEstadoEmpleado(EEstadoEmpleado.ACTIVO);
        EmpleadoEntity guardado= empleadosRepositorio.save(empleado);
        return empleadoMapper.convertToDTO(guardado);
    }

    @Transactional
    @Override
    public void borrarEmpleado(UUID IDpublico)
    {
        EmpleadoEntity buscado = empleadosRepositorio.
                findByIdPublico(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));
        buscado.setEstadoEmpleado(EEstadoEmpleado.INACTIVO);
        empleadosRepositorio.save(buscado);
    }

    @Transactional
    @Override
    public EmpleadoDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));

        empleadoMapper.updateEntityFromDTO(empleadoDto, empleado);

        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }


    @Override
    public EmpleadoDTO buscarPorIDpublico(UUID IDpublico) {
        return empleadosRepositorio.
                findByIdPublico(IDpublico)
                .map(empleadoMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró :" , IDpublico.toString()));
    }

    @Override
    public List<EmpleadoDTO> buscarTodos(Integer dniIgual,
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
                                         String provinciaIgual,
                                         String provinciaContiene,
                                         EEstadoEmpleado estadoIgual,
                                         EEstadoEmpleado estadoActivo,
                                         EEstadoEmpleado estadoInactivo) {

        PredicateSpecification<EmpleadoEntity> spec =
                PredicateSpecification.allOf(
                        EmpleadoSpecification.dniIgual(dniIgual),
                        EmpleadoSpecification.dniContiene(dniContiene),
                        EmpleadoSpecification.nombreIgual(nombreIgual),
                        EmpleadoSpecification.nombreContiene(nombreContiene),
                        EmpleadoSpecification.apellidoIgual(apellidoIgual),
                        EmpleadoSpecification.apellidoContiene(apellidoContiene),
                        EmpleadoSpecification.telefonoIgual(telefonoIgual),
                        EmpleadoSpecification.telefonoContiene(telefonoContiene),
                        EmpleadoSpecification.cuitIgual(cuitIgual),
                        EmpleadoSpecification.cuitContiene(cuitContiene),
                        EmpleadoSpecification.sueldoIgual(sueldoIgual),
                        EmpleadoSpecification.sueldoMenor(sueldoMenor),
                        EmpleadoSpecification.sueldoMayor(sueldoMayor),
                        EmpleadoSpecification.sectorIgual(sectorIgual),
                        EmpleadoSpecification.sectorContiene(sectorContiene),
                        EmpleadoSpecification.rolIgual(rolIgual),
                        EmpleadoSpecification.rolContiene(rolContiene),
                        EmpleadoSpecification.calleIgual(calleIgual),
                        EmpleadoSpecification.calleContiene(calleContiene),
                        EmpleadoSpecification.numeroIgual(numeroIgual),
                        EmpleadoSpecification.numeroContiene(numeroContiene),
                        EmpleadoSpecification.ciudadIgual(ciudadIgual),
                        EmpleadoSpecification.ciudadContiene(ciudadContiene),
                        EmpleadoSpecification.provinciaIgual(provinciaIgual),
                        EmpleadoSpecification.provinciaContiene(provinciaContiene),
                        EmpleadoSpecification.estadoIgual(estadoIgual),
                        EmpleadoSpecification.estadoActivo(estadoActivo),
                        EmpleadoSpecification.estadoInactivo(estadoInactivo)
                );


        return empleadosRepositorio.findAll(spec)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

}