package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.MesaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.MesaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MesaMapper {

    public MesaResponseDTO toResponseDTO(MesaEntity entity) {
        if (entity == null) return null;

        MesaResponseDTO dto = new MesaResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        dto.setNumero(entity.getNumero());
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

    public MesaEntity toEntity(MesaRequestDTO dto) {
        if (dto == null) return null;

        MesaEntity entity = new MesaEntity();
        entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
        entity.setCapacidad(dto.getCapacidad());
        return entity;
    }

    public void actualizarDesdeRequest(MesaRequestDTO dto, MesaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
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
