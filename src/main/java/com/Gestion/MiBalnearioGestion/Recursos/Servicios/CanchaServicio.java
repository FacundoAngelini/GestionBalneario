package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
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

    @Transactional
    @Override
    public CanchaDTO crearCancha(CanchaDTO canchaDTO){
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(canchaDTO.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        CanchaEntity canchaEntity = canchaMapper.convertToEntity(canchaDTO, CanchaEntity.class);
        canchaEntity.setEsReservable(true);
        canchaEntity.setSector(sectorDb);
        CanchaEntity guardado = canchaRepositorio.save(canchaEntity);
        return canchaMapper.convertToDTO(guardado);
    }

    @Transactional(readOnly = true)
    @Override
    public CanchaDTO buscarPorId(UUID id){
        CanchaEntity cancha = canchaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una cancha con esta id", "CanchaEntity"));
        return canchaMapper.convertToDTO(cancha);
    }

    @Transactional
    @Override
    public CanchaDTO actualizarCancha(UUID id, CanchaDTO canchaDTO){
        CanchaEntity cancha = canchaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una cancha con esta id", "CanchaEntity"));


        if(!cancha.getSector().getPublicId().equals(canchaDTO.getSectorPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(canchaDTO.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            cancha.setSector(nuevoSector);
        }

        canchaMapper.updateEntityFromDTO(canchaDTO,cancha);
        return canchaMapper.convertToDTO(cancha);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CanchaDTO> buscarTodas(ETipoCancha cancha,
                                       Integer capacidadIgual,
                                       Integer capacidadMenor,
                                       Integer capacidadMayor,
                                       Boolean iluminacion){

        PredicateSpecification<CanchaEntity> spec=
                PredicateSpecification.allOf(
                        CanchaSpecification.tipoDeCancha(cancha),
                        CanchaSpecification.capacidadIgual(capacidadIgual),
                        CanchaSpecification.capacidadMenor(capacidadMenor),
                        CanchaSpecification.capacidadMayor(capacidadMayor),
                        CanchaSpecification.iluminacion(iluminacion)
                );

        return canchaRepositorio.findAll(spec)
                .stream()
                .map(canchaMapper::convertToDTO)
                .toList();

    }

}
