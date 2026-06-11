package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.RecursoRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.RecursoResponseDTO;
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
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional(readOnly = true)
    public RecursoResponseDTO buscarPorPublicId(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se encontró el recurso con el UUID especificado", "RecursoEntity"));
        return recursoMapper.convertToDTO(recurso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecursoResponseDTO> buscarTodos(String nombreIgual,
                                                String nombreContiene,
                                                Boolean reservableVerdad) {
        PredicateSpecification<RecursoEntity> spec = PredicateSpecification.allOf(
                RecursoSpecification.nombreContiene(nombreContiene),
                RecursoSpecification.nombreIgual(nombreIgual),
                RecursoSpecification.reservableVerdad(reservableVerdad)
        );

        return recursoRepositorio.findAll(spec)
                .stream()
                .map(recursoMapper::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecursoResponseDTO> buscarPorSector(UUID sectorPublicId) {
        if (!sectorRepositorio.existsByPublicId(sectorPublicId)) {
            throw new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity");
        }
        return recursoRepositorio.findBySectorPublicId(sectorPublicId)
                .stream()
                .map(recursoMapper::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarRecurso(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se encontró el recurso a desactivar", "RecursoEntity"));
        recurso.setEsReservable(false);
        recursoRepositorio.save(recurso);
    }

    @Override
    @Transactional
    public void activarRecurso(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se encontró el recurso a activar", "RecursoEntity"));
        recurso.setEsReservable(true);
        recursoRepositorio.save(recurso);
    }

    @Override
    @Transactional
    public void desactivarTodoElInventario() {
        recursoRepositorio.desactivarTodoElInventario();
    }

    @Override
    @Transactional
    public void borrarTodoElInventario() {
        long activos = recursoRepositorio.countByEsReservableTrue();
        if (activos > 0) {
            throw new RuntimeException(
                    "Operación rechazada. No podés borrar el inventario porque existen "
                            + activos + " recursos activos. Primero debés pasarlos a no disponibles.");
        }
        recursoRepositorio.deleteAll();
    }

    @Override
    @Transactional
    public void borrarRecurso(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Recurso no encontrado", publicId.toString()));
        recursoRepositorio.delete(recurso);
    }
}