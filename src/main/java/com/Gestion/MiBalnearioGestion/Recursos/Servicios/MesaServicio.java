package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.MesaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.MesaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.MesaMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.MesaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IMesaServcio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.MesaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MesaServicio implements IMesaServcio {

    private final MesaRepositorio mesaRepositorio;
    private final MesaMapper mesaMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public MesaResponseDTO crearMesa(MesaRequestDTO dto) {
        if (mesaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una mesa con ese número", "MesaEntity");
        }

        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));

        MesaEntity mesa = mesaMapper.toEntity(dto);
        mesa.setEsReservable(true);
        mesa.setSector(sector);

        return mesaMapper.toResponseDTO(mesaRepositorio.save(mesa));
    }

    @Override
    @Transactional
    public MesaResponseDTO actualizarMesa(UUID id, MesaRequestDTO dto) {
        MesaEntity mesa = mesaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada", "MesaEntity"));

        if (!mesa.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));
            mesa.setSector(nuevoSector);
        }

        if (mesa.getNumero() != dto.getNumero() &&
                mesaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una mesa con ese número", "MesaEntity");
        }

        mesaMapper.actualizarDesdeRequest(dto, mesa);
        return mesaMapper.toResponseDTO(mesaRepositorio.save(mesa));
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponseDTO buscarPorId(UUID id) {
        return mesaRepositorio.findByPublicId(id)
                .map(mesaMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada", "MesaEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                             Integer capacidadIgual, Integer capacidadMayor, Integer capacidadMenor) {
        PredicateSpecification<MesaEntity> spec = PredicateSpecification.allOf(
                MesaSpecification.numeroIgual(numero),
                MesaSpecification.numeroMayor(numeroMayor),
                MesaSpecification.numeroMenor(numeroMenor),
                MesaSpecification.capacidadIgual(capacidadIgual),
                MesaSpecification.capacidadMayor(capacidadMayor),
                MesaSpecification.capacidadMenor(capacidadMenor)
        );

        return mesaRepositorio.findAll(spec)
                .stream()
                .map(mesaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarMesa(UUID id) {
        MesaEntity mesa = mesaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada", "MesaEntity"));
        mesa.setEsReservable(false);
        mesaRepositorio.save(mesa);
    }
}