package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PiletaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PiletaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class PiletaMapper {

    public PiletaResponseDTO toResponseDTO(PiletaEntity entity) {
        if (entity == null) return null;

        PiletaResponseDTO dto = new PiletaResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        dto.setEsClimatizada(entity.isEsClimatizada());
        dto.setTamanio(entity.getTamanio());

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

    public PiletaEntity toEntity(PiletaRequestDTO dto) {
        if (dto == null) return null;

        PiletaEntity entity = new PiletaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsClimatizada(dto.isEsClimatizada());
        entity.setTamanio(dto.getTamanio());
        return entity;
    }

    public void actualizarDesdeRequest(PiletaRequestDTO dto, PiletaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        entity.setEsClimatizada(dto.isEsClimatizada());
        entity.setTamanio(dto.getTamanio());
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
