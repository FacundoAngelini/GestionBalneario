package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CarpaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CarpaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CarpaMapper {

    public CarpaResponseDTO toResponseDTO(CarpaEntity entity) {
        if (entity == null) return null;

        CarpaResponseDTO dto = new CarpaResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        dto.setNumero(entity.getNumero());
        dto.setPasillo(entity.getPasillo());
        dto.setCapacidad(entity.getCapacidad());

        if (entity.getSector() != null) {
            dto.setSectorPublicId(entity.getSector().getPublicId());
            dto.setSectorNombre(entity.getSector().getNombre());
        }

        if (entity.getPrecioRecurso() != null) {
            dto.setPrecios(entity.getPrecioRecurso().stream()
                    .map(this::mapPrecio)
                    .toList());
        }

        return dto;
    }

    public CarpaEntity toEntity(CarpaRequestDTO dto) {
        if (dto == null) return null;

        CarpaEntity entity = new CarpaEntity();
        entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
        entity.setPasillo(dto.getPasillo());
        entity.setCapacidad(dto.getCapacidad());
        // sector y publicId se setean en el servicio
        return entity;
    }

    public void actualizarDesdeRequest(CarpaRequestDTO dto, CarpaEntity entity) {
        if (dto.getNombre() != null)   entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
        entity.setPasillo(dto.getPasillo());
        entity.setCapacidad(dto.getCapacidad());
    }

    private PrecioRecursoResponseDTO mapPrecio(PrecioRecursoEntity p) {
        PrecioRecursoResponseDTO dto = new PrecioRecursoResponseDTO();
        dto.setPublicId(p.getPublicId());
        dto.setPrecio(p.getPrecio());
        dto.setFechaVigencia(p.getFechaVigencia());
        dto.setFechaCaducada(p.getFechaCaducada());
        if (p.getRecurso() != null) dto.setRecursoPublicId(p.getRecurso().getPublicId());
        return dto;
    }
}