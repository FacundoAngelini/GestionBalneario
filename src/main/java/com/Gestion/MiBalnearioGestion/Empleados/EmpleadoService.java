package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.ExEntidadExistente;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorios.EmpleadosRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadosRepository empleadosRepositorio;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoMapper empleadoMapper;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public EmpleadoEntity crearEmpleado (EmpleadoDTO dtoEmpleado){

        if (empleadosRepositorio.findByDni(dtoEmpleado.getDni()).isPresent()){
            throw new RuntimeException("Ya existe un cliente con ese DNI");
        }

        if (empleadosRepositorio.findByEmail(dtoEmpleado.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }

        if(empleadosRepositorio.findByCuit(dtoEmpleado.getCuit()).isPresent()){
            throw new RuntimeException("Ya existe un cliente con ese cuit");
        }

        UsuarioEntity usuario = usuarioMapper.converToEntity(dtoEmpleado.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dtoEmpleado, EmpleadoEntity.class);
        empleado.setUsuario(usuarioGuardado);
        return empleadosRepositorio.save(empleado);
    }

    @Transactional
    public void borrarEmpleado(UUID IDpublico)
    {
        EmpleadoEntity buscado = empleadosRepositorio.
        findByIdPublico(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));
        empleadosRepositorio.delete(buscado);
    }

    @Transactional
    public EmpleadoDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));

       EmpleadoEntity actualizado = empleadosRepositorio.save(empleado);

        return empleadoMapper.convertToDTO(actualizado);
    }

    public EmpleadoDTO buscarPorIDpublico(UUID IDpublico) {
        return empleadosRepositorio.
                findByIdPublico(IDpublico)
                .map(empleadoMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró :" , IDpublico.toString()));
    }


    public List<EmpleadoDTO> findAll() {
        return empleadosRepositorio.findAll().
                stream().
                map(empleadoMapper::convertToDTO).
                toList();
    }

}
