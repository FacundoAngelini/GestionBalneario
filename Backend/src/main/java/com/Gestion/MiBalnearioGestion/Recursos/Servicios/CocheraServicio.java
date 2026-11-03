package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.Entity.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
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
    private final SectorRepositorio sectorRepositorio;
    private final CocheraMapper cocheraMapper;
    private final RecursoRepositorio recursoRepositorio;

    @Transactional
    @Override
    public CocheraDTO crearCochera(CocheraDTO dto) {
        if(cocheraRepositorio.findByNumeroCochera(dto.getNumeroCochera()).isPresent()){
            throw new EntidadExistenteException("Ya existe una cochera con este numero", "CocheraEntity");
        }
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        CocheraEntity cochera = cocheraMapper.convertToEntity(dto, CocheraEntity.class);
        cochera.setSector(sectorDb);
        cochera.setEsReservable(true);
        CocheraEntity guardado =  cocheraRepositorio.save(cochera);
        return cocheraMapper.convertToDTO(guardado);
    }

    @Transactional
    @Override
    public CocheraDTO actualizarCochera(UUID id, CocheraDTO dto) {
        CocheraEntity cochera = cocheraRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro ninguna cochera con ese id", "CocheraEntity"));

        if(!cochera.getSector().getPublicId().equals(dto.getPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            cochera.setSector(nuevoSector);

        }
        cocheraMapper.updateEntityFromDTO(dto,cochera);
        return cocheraMapper.convertToDTO(cochera);
    }

    @Transactional(readOnly = true)
    @Override
    public CocheraDTO buscarCochera(UUID id) {
        CocheraEntity cochera = cocheraRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una cochera con esta id", "CocheraEntity"));
        return cocheraMapper.convertToDTO(cochera);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CocheraDTO> listarCocheras(Integer cocheraIgual,
                                           Integer cocheraMenor,
                                           Integer cocheraMayor) {

        PredicateSpecification<CocheraEntity> spec=
                PredicateSpecification.allOf(
                        CocheraSpecification.cocheraIgual(cocheraIgual),
                        CocheraSpecification.cocheraMenor(cocheraMenor),
                        CocheraSpecification.cocheraMayor(cocheraMayor)
                );

        return cocheraRepositorio
                .findAll(spec)
                .stream()
                .map(cocheraMapper::convertToDTO)
                .toList();

    }


}
