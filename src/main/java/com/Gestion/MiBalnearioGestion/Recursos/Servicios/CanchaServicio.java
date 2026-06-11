package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CanchaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CanchaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.CanchaMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CanchaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICanchaServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.CanchaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CanchaServicio implements ICanchaServicio {

    private final CanchaRepositorio canchaRepositorio;
    private final CanchaMapper canchaMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public CanchaResponseDTO crearCancha(CanchaRequestDTO dto) {
        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));

        CanchaEntity cancha = canchaMapper.toEntity(dto);
        cancha.setEsReservable(true);
        cancha.setSector(sector);

        return canchaMapper.toResponseDTO(canchaRepositorio.save(cancha));
    }

    @Override
    @Transactional
    public CanchaResponseDTO actualizarCancha(UUID id, CanchaRequestDTO dto) {
        CanchaEntity cancha = canchaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cancha no encontrada", "CanchaEntity"));

        if (!cancha.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado", "SectorEntity"));
            cancha.setSector(nuevoSector);
        }

        canchaMapper.actualizarDesdeRequest(dto, cancha);
        return canchaMapper.toResponseDTO(canchaRepositorio.save(cancha));
    }

    @Override
    @Transactional(readOnly = true)
    public CanchaResponseDTO buscarPorId(UUID id) {
        return canchaRepositorio.findByPublicId(id)
                .map(canchaMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cancha no encontrada", "CanchaEntity"));
    }

    @Override
    @Transactional(readOnly = true)
public List<CanchaResponseDTO> buscarTodos(ETipoCancha tipoCancha, Integer capacidadIgual,
                                               Integer capacidadMayor, Integer capacidadMenor,
                                               Boolean iluminacion) {
        PredicateSpecification<CanchaEntity> spec = PredicateSpecification.allOf(
                CanchaSpecification.tipoDeCancha(tipoCancha),
                CanchaSpecification.capacidadIgual(capacidadIgual),
                CanchaSpecification.capacidadMayor(capacidadMayor),
                CanchaSpecification.capacidadMenor(capacidadMenor),
                CanchaSpecification.iluminacion(iluminacion)
        );

        return canchaRepositorio.findAll(spec)
                .stream()
                .map(canchaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarCancha(UUID id) {
        CanchaEntity cancha = canchaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cancha no encontrada", "CanchaEntity"));
        cancha.setEsReservable(false);
        canchaRepositorio.save(cancha);
    }
}
