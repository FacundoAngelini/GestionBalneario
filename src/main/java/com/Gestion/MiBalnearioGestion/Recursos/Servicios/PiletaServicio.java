package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
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
    private final PiletaMapper  piletaMapper;
    private final SectorRepositorio sectorRepositorio;
    private final RecursoRepositorio recursoRepositorio;

    @Transactional
    @Override
    public PiletaDTO crearPileta(PiletaDTO dto){
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        PiletaEntity pileta = piletaMapper.convertToEntity(dto, PiletaEntity.class);
        pileta.setSector(sectorDb);
        pileta.setEsReservable(true);
        PiletaEntity guardado= piletaRepositorio.save(pileta);
        return piletaMapper.convertToDTO(guardado);
    }

    @Transactional
    @Override
    public PiletaDTO actualizarPileta(PiletaDTO dto, UUID id){
        PiletaEntity pileta = piletaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una pileta con este id", "PiletaEntity"));

        if(!pileta.getSector().getPublicId().equals(dto.getSectorPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            pileta.setSector(nuevoSector);
        }
        piletaMapper.updateEntityFromDTO(dto,pileta);
        return piletaMapper.convertToDTO(pileta);
    }

    @Transactional(readOnly = true)
    @Override
    public PiletaDTO obtenerPileta(UUID id){
        PiletaEntity pileta = piletaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una pileta con este id", "PiletaEntity"));

        return piletaMapper.convertToDTO(pileta);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PiletaDTO> obtenerPiletas(Boolean climatizada,
                                          Integer tamanioIgual,
                                          Integer TamanioMayor,
                                          Integer TamanioMenor){

        PredicateSpecification<PiletaEntity> spec =
                PredicateSpecification.allOf(
                        PiletaSpecification.climatizada(climatizada),
                        PiletaSpecification.tamanioIgual(tamanioIgual),
                        PiletaSpecification.tamanioMayor(TamanioMayor),
                        PiletaSpecification.tamanioMenor(TamanioMenor)
                );

        return piletaRepositorio.findAll(spec)
                .stream()
                .map(piletaMapper::convertToDTO)
                .toList();

    }
}
