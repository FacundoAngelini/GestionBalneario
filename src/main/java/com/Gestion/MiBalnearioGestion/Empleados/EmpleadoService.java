package com.Gestion.MiBalnearioGestion.Empleados;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.ExEntidadExistente;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorios.EmpleadosRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
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

        UsuarioEntity usuario = usuarioMapper.converToEntity(dtoEmpleado.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dtoEmpleado, EmpleadoEntity.class);
        empleado.setUsuario(usuarioGuardado);
        empleado.setEstadoEmpleado(EEstadoEmpleado.ACTIVO);
        EmpleadoEntity guardado= empleadosRepositorio.save(empleado);
        return empleadoMapper.convertToDTO(guardado);
    }

    @Transactional
    public void borrarEmpleado(UUID IDpublico)
    {
        EmpleadoEntity buscado = empleadosRepositorio.
        findByIdPublico(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));
        buscado.setEstadoEmpleado(EEstadoEmpleado.INACTIVO);
        empleadosRepositorio.save(buscado);
    }

    @Transactional
    public EmpleadoDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));

       empleadoMapper.updateEntityFromDTO(empleadoDto, empleado);

       if(empleadosRepositorio.findByDni(empleadoDto.getDni())
               .filter(e->!e.getPublicId().equals(IDpublico))
               .isPresent()){
           throw new ExEntidadExistente("Ya existe un empleado con ese dni", "EmpleadoEntity");
       }

       if(empleadosRepositorio.findByEmail(empleadoDto.getEmail())
               .filter(e-> !e.getPublicId().equals(IDpublico))
               .isPresent()) {
           throw new ExEntidadExistente("Ya existe un empleado con ese email", "EmpleadoEntity");
       }

       if(empleadosRepositorio.findByCuit(empleado.getCuit())
               .filter(e->!e.getPublicId().equals(IDpublico))
               .isPresent()){
           throw new ExEntidadExistente("Ya existe un empleado con ese cuit", "EmpleadoEntity");
       }


        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }

    @Transactional
    public EmpleadoDTO actualizarSueldo(UUID IDpublico, double sueldo){
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(IDpublico)
                .orElseThrow(()->new EntidadNoEncontradaException("Empleado no encntrado :", IDpublico.toString()));
        empleado.setSueldo(sueldo);
        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }

    @Transactional
    public EmpleadoDTO cambiarEstado(UUID idPublico, EEstadoEmpleado nuevoEstado) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(idPublico)
                .orElseThrow(()->new EntidadNoEncontradaException("Empleado no encontrado", idPublico.toString()));
        empleado.setEstadoEmpleado(nuevoEstado);
        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }

    @Transactional
    public EmpleadoDTO cambiarRol(UUID idPublico, RolEntity rol) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByIdPublico(idPublico)
                .orElseThrow(()->new EntidadNoEncontradaException("Empleado no encontrado", idPublico.toString()));
        empleado.setRol(rol);
        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }

    @Transactional
    public EmpleadoDTO cambiarSector(UUID idPublico, SectorEntity sector) {
        EmpleadoEntity empleado =empleadosRepositorio
                .findByIdPublico(idPublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Empleado no encontrado", idPublico.toString()));
        empleado.setSector(sector);
        return empleadoMapper.convertToDTO(empleadosRepositorio.save(empleado));
    }

    public EmpleadoDTO buscarPorIDpublico(UUID IDpublico) {
        return empleadosRepositorio.
                findByIdPublico(IDpublico)
                .map(empleadoMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró :" , IDpublico.toString()));
    }


    public List<EmpleadoDTO> buscarTodos() {
        return empleadosRepositorio.findAll().
                stream().
                map(empleadoMapper::convertToDTO).
                toList();
    }

    public List<EmpleadoDTO> buscarPorEstado(EEstadoEmpleado estado) {
        return empleadosRepositorio.findByEstado(estado)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorEstadoActivo(EEstadoEmpleado estado) {
        return buscarPorEstado(EEstadoEmpleado.ACTIVO);
    }

    public List<EmpleadoDTO> buscarPorEstadoInactivo(EEstadoEmpleado estado) {
        return buscarPorEstado(EEstadoEmpleado.INACTIVO);
    }

    public List<EmpleadoDTO> buscarPorRol(RolEntity rol){
        return empleadosRepositorio.findByRol(rol)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorSector(SectorEntity sector){
        return empleadosRepositorio.findBySector(sector)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorNombre(String nombre){
        return empleadosRepositorio.findByNombre(nombre)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorApellido(String apellido){
        return empleadosRepositorio.findByApellido(apellido)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorCuit(String cuit){
        return empleadosRepositorio.findByCuit(cuit)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorEmail(String email){
        return empleadosRepositorio.findByEmail(email)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorDni(int dni){
        return empleadosRepositorio.findByDni(dni)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorNombreYApellido(String nombre, String apellido){
        return empleadosRepositorio.findByNombreAndApellido(nombre,apellido)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }

    public List<EmpleadoDTO> buscarPorSueldo(double sueldo){
        return empleadosRepositorio.findBySueldo(sueldo)
                .stream()
                .map(empleadoMapper::convertToDTO)
                .toList();
    }
}
