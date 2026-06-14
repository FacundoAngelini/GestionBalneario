package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
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
    private final RecursoRepositorio recursoRepositorio;

    @Transactional
    @Override
    public MesaDTO crearMesa(MesaDTO dto){
        if(mesaRepositorio.findByNumero(dto.getNumero()).isPresent()){
            throw new EntidadExistenteException("Ya existe una mesa con esta numero", "MesaEntity");
        }
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        MesaEntity mesa =  mesaMapper.convertToEntity(dto, MesaEntity.class);
        mesa.setSector(sectorDb);
        mesa.setEsReservable(true);
        MesaEntity guardado = mesaRepositorio.save(mesa);
        return mesaMapper.convertToDTO(guardado);
    }

    @Transactional
    @Override
    public MesaDTO actualizarMesa(MesaDTO dto, UUID id){
        MesaEntity mesa = mesaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro una mesa con esa id", "MesaEntity"));
        if(!mesa.getSector().getPublicId().equals(dto.getSectorPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            mesa.setSector(nuevoSector);

        }
        mesaMapper.updateEntityFromDTO(dto, mesa);
        return mesaMapper.convertToDTO(mesa);
    }

    @Transactional(readOnly = true)
    @Override
    public MesaDTO obtenerMesaId(UUID id){
        MesaEntity mesa = mesaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro  una mesa con esa id", "MesaEntity"));
        return mesaMapper.convertToDTO(mesa);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MesaDTO> obtenerMesas(Integer numeroIgual,
                                      Integer numeroMenor,
                                      Integer numeroMayor,
                                      Integer capacidadIgual,
                                      Integer capacidadMenor,
                                      Integer capacidadMayor){

        PredicateSpecification<MesaEntity> spec=
                PredicateSpecification.allOf(
                        MesaSpecification.numeroIgual(numeroIgual),
                        MesaSpecification.numeroMenor(numeroMenor),
                        MesaSpecification.numeroMayor(numeroMayor),
                        MesaSpecification.capacidadIgual(capacidadIgual),
                        MesaSpecification.capacidadMenor(capacidadMenor),
                        MesaSpecification.capacidadMayor(capacidadMayor)
                );
        return mesaRepositorio.findAll(spec)
                .stream()
                .map(mesaMapper::convertToDTO)
                .toList();

    }

}
