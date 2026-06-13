package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
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

    @Transactional
    @Override
    public SombrillaDTO crearSombrilla(SombrillaDTO dto){
        if(sombrillaRepositorio.findByPublicId(dto.getPublicID()).isPresent()){
            throw new EntidadExistenteException("Ya existe una sombrilla con esta id", "SombrillaEntity");
        }
        if(sombrillaRepositorio.findByNumero(dto.getNumero()).isPresent()){
            throw new EntidadExistenteException("Ya existe una sombrilla con esta numero", "SombrillaEntity");
        }

        SectorEntity sectorDb = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        SombrillaEntity sombrilla = sombrillaMapper.convertToEntity(dto, SombrillaEntity.class);
        sombrilla.setSector(sectorDb);
        sombrilla.setEsReservable(true);
        SombrillaEntity guardado = sombrillaRepositorio.save(sombrilla);
        return sombrillaMapper.convertToDTO(guardado);
    }

    @Transactional
    @Override
    public SombrillaDTO actualizarSombrilla (SombrillaDTO dto, UUID id){
        SombrillaEntity sombrilla = sombrillaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una sombrilla con esa id", "SombrillaEntity"));

        if(!sombrilla.getSector().getPublicId().equals(dto.getSectorPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            sombrilla.setSector(nuevoSector);
        }

        sombrillaMapper.updateEntityFromDTO(dto,sombrilla);
        return sombrillaMapper.convertToDTO(sombrilla);
    }

    @Transactional(readOnly = true)
    @Override
    public SombrillaDTO buscarPorId(UUID id){
        SombrillaEntity sombrilla = sombrillaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
        return  sombrillaMapper.convertToDTO(sombrilla);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SombrillaDTO> buscarTodas(Integer numero,
                                          Integer numeroMenor,
                                          Integer numeroMayor,
                                          EtamanioSombrilla tamanio){
        PredicateSpecification<SombrillaEntity> spec =
                PredicateSpecification.allOf(
                        SombrillaSpecification.numeroIgual(numero),
                        SombrillaSpecification.numeroMenor(numeroMenor),
                        SombrillaSpecification.numeroMayor(numeroMayor),
                        SombrillaSpecification.tamanioIgual(tamanio)
                );

        return sombrillaRepositorio
                .findAll(spec)
                .stream()
                .map(sombrillaMapper::convertToDTO)
                .toList();

    }

}
