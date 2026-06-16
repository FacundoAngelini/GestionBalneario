package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PrecioRecursoDTO;
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
    private final PrecioRecursoMapper  precioRecursoMapper;
    private final RecursoRepositorio recursoRepositorio;

    @Transactional
    @Override
    public PrecioRecursoDTO crearPrecio(PrecioRecursoDTO dto){

        RecursoEntity recurso= recursoRepositorio.findByPublicId(dto.getRecursoPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Recurso no encontrado", "RecursoEntity"));

        if (dto.getFechaCaducada() != null && dto.getFechaVigencia().isAfter(dto.getFechaCaducada())) {
            throw new DatosInvalidoException("La fecha de vigencia no puede ser posterior a la de caducidad", "PrecioRecursoEntity");
        }
        LocalDate fin = (dto.getFechaCaducada() != null) ? dto.getFechaCaducada() : LocalDate.of(2099, 12, 31); // Por si es vigencia indefinida

        boolean seSolapa = precioRecursoRepositorio
                .existsByRecurso_PublicIdAndFechaVigenciaLessThanEqualAndFechaCaducadaGreaterThanEqual(
                        dto.getRecursoPublicId(), fin, dto.getFechaVigencia()
                );

        if (seSolapa) {
            throw new DatosInvalidoException("Ya existe un precio configurado para este recurso que se solapa con las fechas ingresadas", "PrecioRecursoEntity");
        }
        PrecioRecursoEntity precioRecurso = precioRecursoMapper.convertToEntity(dto, PrecioRecursoEntity.class);
        precioRecurso.setRecurso(recurso);

        PrecioRecursoEntity guardado= precioRecursoRepositorio.save(precioRecurso);
        return precioRecursoMapper.convertToDTO(guardado);
    }

    @Transactional(readOnly = true)
    @Override
    public PrecioRecursoDTO buscarPorPublicId(UUID publicId) {
        PrecioRecursoEntity precio = precioRecursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro el precio", "PrecioRecursoEntity"));
        return precioRecursoMapper.convertToDTO(precio);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PrecioRecursoDTO> buscarTodos(LocalDate precioVigenciaIgual,
                                              LocalDate precioVigenciaMenor,
                                              LocalDate precioVigenciaMayor,
                                              LocalDate precioCaducidoIgual,
                                              LocalDate precioCaducidoMenor,
                                              LocalDate precioCaducidoMayor,
                                              Double precioIgual,
                                              Double precioMenor,
                                              Double precioMayor
                                              ){

        PredicateSpecification<PrecioRecursoEntity> spec =
                PredicateSpecification.allOf(
                        PrecioRecursoSpecification.fechaVigenciaIgual(precioVigenciaIgual),
                        PrecioRecursoSpecification.fechaVigenciaMenor(precioVigenciaMenor),
                        PrecioRecursoSpecification.fechaVigenciaMayor(precioVigenciaMayor),
                        PrecioRecursoSpecification.fechaCaducadaIgual(precioCaducidoIgual),
                        PrecioRecursoSpecification.fechaCaducadaMenor(precioCaducidoMenor),
                        PrecioRecursoSpecification.fechaCaducadaMayor(precioCaducidoMayor),
                        PrecioRecursoSpecification.precioIgual(precioIgual),
                        PrecioRecursoSpecification.precioMayor(precioMayor),
                        PrecioRecursoSpecification.precioMenor(precioMenor)
                );
        return precioRecursoRepositorio.findAll(spec)
                .stream()
                .map(precioRecursoMapper::convertToDTO)
                .toList();

    }
    @Transactional
    @Override
    public PrecioRecursoDTO actualizarPrecio(UUID publicId, PrecioRecursoDTO dto) {
        PrecioRecursoEntity precio = precioRecursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Precio no encontrado", "PrecioRecursoEntity"));

        if (dto.getFechaCaducada() != null && dto.getFechaVigencia().isAfter(dto.getFechaCaducada())) {
            throw new DatosInvalidoException("La fecha de vigencia no puede ser posterior a la de caducidad", "PrecioRecursoEntity");
        }

        precio.setPrecio(dto.getPrecio());
        precio.setFechaVigencia(dto.getFechaVigencia());
        precio.setFechaCaducada(dto.getFechaCaducada());

        if (!precio.getRecurso().getPublicId().equals(dto.getRecursoPublicId())) {
            RecursoEntity recurso = recursoRepositorio.findByPublicId(dto.getRecursoPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Recurso no encontrado", "RecursoEntity"));
            precio.setRecurso(recurso);
        }

        return precioRecursoMapper.convertToDTO(precioRecursoRepositorio.save(precio));
    }
}
