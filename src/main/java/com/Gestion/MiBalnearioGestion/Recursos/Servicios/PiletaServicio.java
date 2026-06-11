package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PiletaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PiletaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.PiletaMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.PiletaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPiletaServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.PiletaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PiletaServicio implements IPiletaServicio {

    private final PiletaRepositorio piletaRepositorio;
    private final PiletaMapper piletaMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public PiletaResponseDTO crearPileta(PiletaRequestDTO dto) {
        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));

        PiletaEntity pileta = piletaMapper.toEntity(dto);
        pileta.setEsReservable(true);
        pileta.setSector(sector);

        return piletaMapper.toResponseDTO(piletaRepositorio.save(pileta));
    }

    @Override
    @Transactional
    public PiletaResponseDTO actualizarPileta(UUID id, PiletaRequestDTO dto) {
        PiletaEntity pileta = piletaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pileta no encontrada", "PiletaEntity"));

        if (!pileta.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));
            pileta.setSector(nuevoSector);
        }

        piletaMapper.actualizarDesdeRequest(dto, pileta);
        return piletaMapper.toResponseDTO(piletaRepositorio.save(pileta));
    }

    @Override
    @Transactional(readOnly = true)
    public PiletaResponseDTO buscarPorId(UUID id) {
        return piletaRepositorio.findByPublicId(id)
                .map(piletaMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pileta no encontrada", "PiletaEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PiletaResponseDTO> buscarTodos(Boolean esClimatizada, Integer tamanio) {
        PredicateSpecification<PiletaEntity> spec = PredicateSpecification.allOf(
                PiletaSpecification.climatizada(esClimatizada),
                PiletaSpecification.tamanioIgual(tamanio)
        );

        return piletaRepositorio.findAll(spec)
                .stream()
                .map(piletaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarPileta(UUID id) {
        PiletaEntity pileta = piletaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pileta no encontrada", "PiletaEntity"));
        pileta.setEsReservable(false);
        piletaRepositorio.save(pileta);
    }
}