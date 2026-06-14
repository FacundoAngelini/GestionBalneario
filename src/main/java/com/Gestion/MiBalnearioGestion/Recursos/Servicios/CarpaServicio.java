package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.CarpaMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CarpaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICarpaServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.CarpaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarpaServicio implements ICarpaServicio {
    private final CarpaRepositorio  carpaRepositorio;
    private final CarpaMapper  carpaMapper;
    private final SectorRepositorio sectorRepositorio;
    private final RecursoRepositorio recursoRepositorio;

    @Transactional
    @Override
    public CarpaDTO crearCarpa(CarpaDTO carpa){
        if(carpaRepositorio.findByNumero(carpa.getNumero()).isPresent()){
            throw new EntidadExistenteException("Ya existe na carpa con este numero", "CarpaEntity");
        }
        SectorEntity sectorDb = sectorRepositorio.findByPublicId(carpa.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el Sector con el UUID especificado", "SectorEntity"));

        CarpaEntity carpaEntity = carpaMapper.convertToEntity(carpa, CarpaEntity.class);
        carpaEntity.setEsReservable(true);
        carpaEntity.setSector(sectorDb);
        CarpaEntity guardado =  carpaRepositorio.save(carpaEntity);
        return carpaMapper.convertToDTO(guardado);
    }

    @Override
    @Transactional
    public CarpaDTO actualizarCarpa(CarpaDTO carpa, UUID id){
        CarpaEntity carpaEntity = carpaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro la carpa con el id ingresado", "CarpaEntity"));

        if(!carpaEntity.getSector().getPublicId().equals(carpa.getSectorPublicId())){
            SectorEntity nuevoSector= sectorRepositorio.findByPublicId(carpa.getSectorPublicId())
                    .orElseThrow(()->new EntidadNoEncontradaException("No se encontro el sector", "SectorEntity"));
            carpaEntity.setSector(nuevoSector);
        }

        carpaMapper.updateEntityFromDTO(carpa,carpaEntity);
        return carpaMapper.convertToDTO(carpaEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CarpaDTO buscarPorId(UUID id){
        CarpaEntity carpa = carpaRepositorio
                .findByPublicId(id)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro la carpa con el id ingresado", "CarpaEntity"));
        return carpaMapper.convertToDTO(carpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarpaDTO> buscarTodos(Integer numero,
                                      Integer numeroMayor,
                                      Integer numeroMenor,
                                      Integer pasilloIgual,
                                      Integer pasilloMayor,
                                      Integer pasilloMenor,
                                      Integer capacidadIgual
                                      ){
        PredicateSpecification<CarpaEntity> spec=
                PredicateSpecification.allOf(
                        CarpaSpecification.numeroIgual(numero),
                        CarpaSpecification.numeroMayor(numeroMayor),
                        CarpaSpecification.numeroMenor(numeroMenor),
                        CarpaSpecification.pasilloIgual(pasilloIgual),
                        CarpaSpecification.pasilloMayor(pasilloMayor),
                        CarpaSpecification.pasilloMenor(pasilloMenor),
                        CarpaSpecification.capacidadIgual(capacidadIgual)
                );

        return carpaRepositorio.findAll(spec)
                .stream()
                .map(carpaMapper::convertToDTO)
                .toList();

    }
}
