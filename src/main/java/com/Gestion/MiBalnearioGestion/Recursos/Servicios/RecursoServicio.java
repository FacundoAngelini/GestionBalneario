package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.RecursoMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;

import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.RecursoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RecursoServicio implements IRecursoServicio {
    private final RecursoRepositorio recursoRepositorio;
    private final RecursoMapper recursoMapper;


    @Transactional(readOnly = true)
    @Override
    public RecursoDTO buscarPorPublicId(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("No se encontro el recurso con el UUID especificado"));
        return recursoMapper.convertToDTO(recurso);
    }

    @Transactional
    @Override
    public void desactivarRecurso(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el recurso a desactivar", "RecursoEntity"));
        recurso.setEsReservable(false);
        recursoRepositorio.save(recurso);
    }

    @Transactional
    @Override
    public void desactivarTodoElInventario() {
        recursoRepositorio.desactivarTodoElInventario();
    }

    @Override
    public void borrarTodoElInventario() {
        long activos = recursoRepositorio.countByEsReservableTrue();

        if (activos > 0) {
            throw new RuntimeException("Operación rechazada.No podés borrar el inventario porque existen "
                    + activos + " recursos activos. Primero debés pasarlos a no disponibles");
        }
        recursoRepositorio.deleteAll();
    }
    @Transactional(readOnly = true)
    @Override
    public List<RecursoDTO> buscarTodos(String nombreIgual,
                                        String nombreContiene,
                                        Boolean reservableVerdad){

        PredicateSpecification<RecursoEntity> spec =
                PredicateSpecification.allOf(
                        RecursoSpecification.nombreContiene(nombreContiene),
                        RecursoSpecification.nombreIgual(nombreIgual),
                        RecursoSpecification.reservableVerdad(reservableVerdad)
                );


        return recursoRepositorio.findAll(spec)
                .stream()
                .map(recursoMapper::convertToDTO)
                .toList();
    }

   @Transactional
    @Override
    public void borrarRecurso(UUID IdPublico) {
        RecursoEntity buscado = recursoRepositorio
                .findByPublicId(IdPublico)
                .orElseThrow(()->new EntidadNoEncontradaException("Recurso no encontrado : ", IdPublico.toString()));
        recursoRepositorio.delete(buscado);
        System.out.println("Recurso eliminado con exito");
    }

}
