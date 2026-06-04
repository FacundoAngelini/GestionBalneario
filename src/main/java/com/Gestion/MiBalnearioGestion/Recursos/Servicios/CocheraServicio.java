package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.CocheraMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CocheraRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CocheraServicio {
    private final CocheraRepositorio cocheraRepositorio;
    private final SectorRepositorio sectorRepositorio;
    private final CocheraMapper cocheraMapper;

    @Transactional
    public CocheraDTO crearCochera(CocheraDTO dto) {
        if(cocheraRepositorio.findByPublicId(dto.getPublicID()).isPresent()){
            throw new EntidadExistenteException("Ya existe una cochera con este id", "CocheraEntity");
        }
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(dto.getPublicID())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        CocheraEntity cochera = cocheraMapper.convertToEntity(dto, CocheraEntity.class);
        cochera.setSector(sectorDb);
        cochera.setEsReservable(true);
        CocheraEntity guardado =  cocheraRepositorio.save(cochera);
        return cocheraMapper.convertToDTO(guardado);
    }

    @Transactional
    public CocheraDTO actualizarCochera(UUID id, CocheraDTO dto) {
        CocheraEntity cochera = cocheraRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro ninguna cochera con ese id", "CocheraEntity"));

        if(!cochera.getSector().getPublicId().equals(dto.getPublicID())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            cochera.setSector(nuevoSector);

        }
        cocheraMapper.updateToEntityFromDTO(dto,cochera);
        return cocheraMapper.convertToDTO(cochera);
    }


}
