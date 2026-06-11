package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.SombrillaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.SombrillaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.SombrillaMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.SombrillaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ISombrillaServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.SombrillaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class SombrillaServicio implements ISombrillaServicio {

    private final SombrillaRepositorio sombrillaRepositorio;
    private final SombrillaMapper sombrillaMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public SombrillaResponseDTO crearSombrilla(SombrillaRequestDTO dto) {
        if (sombrillaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una sombrilla con ese número", "SombrillaEntity");
        }

        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));

        SombrillaEntity sombrilla = sombrillaMapper.toEntity(dto);
        sombrilla.setEsReservable(true);
        sombrilla.setSector(sector);

        return sombrillaMapper.toResponseDTO(sombrillaRepositorio.save(sombrilla));
    }

    @Override
    @Transactional
    public SombrillaResponseDTO actualizarSombrilla(UUID id, SombrillaRequestDTO dto) {
        SombrillaEntity sombrilla = sombrillaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sombrilla no encontrada", "SombrillaEntity"));

        if (!sombrilla.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));
            sombrilla.setSector(nuevoSector);
        }

        if (sombrilla.getNumero() != dto.getNumero() &&
                sombrillaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una sombrilla con ese número", "SombrillaEntity");
        }

        sombrillaMapper.actualizarDesdeRequest(dto, sombrilla);
        return sombrillaMapper.toResponseDTO(sombrillaRepositorio.save(sombrilla));
    }

    @Override
    @Transactional(readOnly = true)
    public SombrillaResponseDTO buscarPorId(UUID id) {
        return sombrillaRepositorio.findByPublicId(id)
                .map(sombrillaMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sombrilla no encontrada", "SombrillaEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SombrillaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                                  EtamanioSombrilla etamano) {
        PredicateSpecification<SombrillaEntity> spec = PredicateSpecification.allOf(
                SombrillaSpecification.numeroIgual(numero),
                SombrillaSpecification.numeroMayor(numeroMayor),
                SombrillaSpecification.numeroMenor(numeroMenor),
                SombrillaSpecification.tamanioIgual(etamano)
        );

        return sombrillaRepositorio.findAll(spec)
                .stream()
                .map(sombrillaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarSombrilla(UUID id) {
        SombrillaEntity sombrilla = sombrillaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sombrilla no encontrada", "SombrillaEntity"));
        sombrilla.setEsReservable(false);
        sombrillaRepositorio.save(sombrilla);
    }
}