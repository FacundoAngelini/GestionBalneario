package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CocheraRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CocheraResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.CocheraMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CocheraRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICocheraServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.CocheraSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CocheraServicio implements ICocheraServicio {

    private final CocheraRepositorio cocheraRepositorio;
    private final CocheraMapper cocheraMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public CocheraResponseDTO crearCochera(CocheraRequestDTO dto) {
        if (cocheraRepositorio.findByNumeroCochera(dto.getNumero_cochera()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una cochera con ese número", "CocheraEntity");
        }

        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));

        CocheraEntity cochera = cocheraMapper.toEntity(dto);
        cochera.setEsReservable(true);
        cochera.setSector(sector);

        return cocheraMapper.toResponseDTO(cocheraRepositorio.save(cochera));
    }

    @Override
    @Transactional
    public CocheraResponseDTO actualizarCochera(UUID id, CocheraRequestDTO dto) {
        CocheraEntity cochera = cocheraRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cochera no encontrada", "CocheraEntity"));

        if (!cochera.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));
            cochera.setSector(nuevoSector);
        }

        if (cochera.getNumeroCochera() != dto.getNumero_cochera() &&
                cocheraRepositorio.findByNumeroCochera(dto.getNumero_cochera()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una cochera con ese número", "CocheraEntity");
        }

        cocheraMapper.actualizarDesdeRequest(dto, cochera);
        return cocheraMapper.toResponseDTO(cocheraRepositorio.save(cochera));
    }

    @Override
    @Transactional(readOnly = true)
    public CocheraResponseDTO buscarPorId(UUID id) {
        return cocheraRepositorio.findByPublicId(id)
                .map(cocheraMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cochera no encontrada", "CocheraEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CocheraResponseDTO> buscarTodos(Integer numeroCochera, Integer numeroMayor, Integer numeroMenor) {
        PredicateSpecification<CocheraEntity> spec = PredicateSpecification.allOf(
                CocheraSpecification.cocherIgual(numeroCochera),
                CocheraSpecification.cocheraMayor(numeroMayor),
                CocheraSpecification.cocherMenor(numeroMenor)
        );

        return cocheraRepositorio.findAll(spec)
                .stream()
                .map(cocheraMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarCochera(UUID id) {
        CocheraEntity cochera = cocheraRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cochera no encontrada", "CocheraEntity"));
        cochera.setEsReservable(false);
        cocheraRepositorio.save(cochera);
    }
}
