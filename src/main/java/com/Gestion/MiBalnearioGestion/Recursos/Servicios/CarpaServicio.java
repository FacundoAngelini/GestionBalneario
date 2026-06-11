package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CarpaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CarpaResponseDTO;
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

    private final CarpaRepositorio carpaRepositorio;
    private final CarpaMapper carpaMapper;
    private final SectorRepositorio sectorRepositorio;

    @Override
    @Transactional
    public CarpaResponseDTO crearCarpa(CarpaRequestDTO dto) {
        if (carpaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una carpa con ese número", "CarpaEntity");
        }

        SectorEntity sector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Sector no encontrado", "SectorEntity"));

        CarpaEntity carpa = carpaMapper.toEntity(dto);
        carpa.setEsReservable(true);
        carpa.setSector(sector);

        return carpaMapper.toResponseDTO(carpaRepositorio.save(carpa));
    }

    @Override
    @Transactional
    public CarpaResponseDTO actualizarCarpa(UUID id, CarpaRequestDTO dto) {
        CarpaEntity carpa = carpaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Carpa no encontrada", "CarpaEntity"));

        // Solo buscamos el sector si cambió
        if (!carpa.getSector().getPublicId().equals(dto.getSectorPublicId())) {
            SectorEntity nuevoSector = sectorRepositorio.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "Sector no encontrado", "SectorEntity"));
            carpa.setSector(nuevoSector);
        }

        // Validamos número solo si cambió
        if (carpa.getNumero() != dto.getNumero() &&
                carpaRepositorio.findByNumero(dto.getNumero()).isPresent()) {
            throw new EntidadExistenteException("Ya existe una carpa con ese número", "CarpaEntity");
        }

        carpaMapper.actualizarDesdeRequest(dto, carpa);
        return carpaMapper.toResponseDTO(carpaRepositorio.save(carpa));
    }

    @Override
    @Transactional(readOnly = true)
    public CarpaResponseDTO buscarPorId(UUID id) {
        return carpaRepositorio.findByPublicId(id)
                .map(carpaMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Carpa no encontrada", "CarpaEntity"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarpaResponseDTO> buscarTodos(Integer numero, Integer numeroMayor, Integer numeroMenor,
                                              Integer pasilloIgual, Integer pasilloMayor, Integer pasilloMenor,
                                              Integer capacidadIgual) {
        PredicateSpecification<CarpaEntity> spec = PredicateSpecification.allOf(
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
                .map(carpaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void desactivarCarpa(UUID id) {
        CarpaEntity carpa = carpaRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Carpa no encontrada", "CarpaEntity"));
        carpa.setEsReservable(false);
        carpaRepositorio.save(carpa);
    }
}