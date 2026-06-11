package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PrecioRequestRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.PrecioRecursoMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.PrecioRecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPrecioRecursoServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.PrecioRecursoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PrecioRecursoServicio implements IPrecioRecursoServicio {

    private final PrecioRecursoRepositorio precioRecursoRepositorio;
    private final PrecioRecursoMapper precioRecursoMapper;
    private final RecursoRepositorio recursoRepositorio;

    @Override
    @Transactional
    public PrecioRecursoResponseDTO crearPrecio(PrecioRequestRecursoDTO dto) {
        if (dto.getFechaCaducada().isBefore(dto.getFechaVigencia())) {
            throw new IllegalArgumentException(
                    "La fecha de caducidad no puede ser anterior a la de vigencia");
        }

        RecursoEntity recurso = recursoRepositorio.findByPublicId(dto.getRecursoPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Recurso no encontrado", "RecursoEntity"));

        PrecioRecursoEntity precio = precioRecursoMapper.toEntity(dto);
        precio.setRecurso(recurso);

        return precioRecursoMapper.toResponseDTO(precioRecursoRepositorio.save(precio));
    }

    @Override
    @Transactional(readOnly = true)
    public PrecioRecursoResponseDTO buscarPorPublicId(UUID publicId) {
        return precioRecursoRepositorio.findByPublicId(publicId)
                .map(precioRecursoMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Precio no encontrado", "PrecioRecursoEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioRecursoResponseDTO> buscarPorRecurso(UUID recursoPublicId) {
        return precioRecursoRepositorio.findByRecursoPublicId(recursoPublicId)
                .stream()
                .map(precioRecursoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioRecursoResponseDTO> buscarTodos(
            LocalDate vigenciaIgual, LocalDate vigenciaMenor, LocalDate vigenciaMayor,
            LocalDate caducadaIgual, LocalDate caducadaMenor, LocalDate caducadaMayor,
            Double precioIgual, LocalDate precioMenor, Double precioMayor) {

        PredicateSpecification<PrecioRecursoEntity> spec = PredicateSpecification.allOf(
                PrecioRecursoSpecification.fechaVigenciaIgual(vigenciaIgual),
                PrecioRecursoSpecification.fechaVigenciaMenor(vigenciaMenor),
                PrecioRecursoSpecification.fechaVigenciaMayor(vigenciaMayor),
                PrecioRecursoSpecification.fechaCaducadaIgual(caducadaIgual),
                PrecioRecursoSpecification.fechaCaducadaMenor(caducadaMenor),
                PrecioRecursoSpecification.fechaCaducadaMayor(caducadaMayor),
                PrecioRecursoSpecification.precioIgual(precioIgual),
                PrecioRecursoSpecification.precioMayor(precioMayor)
        );

        return precioRecursoRepositorio.findAll(spec)
                .stream()
                .map(precioRecursoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarPrecio(UUID publicId) {
        PrecioRecursoEntity precio = precioRecursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Precio no encontrado", "PrecioRecursoEntity"));
        precioRecursoRepositorio.delete(precio);
    }
}