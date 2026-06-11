package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.SombrillaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.SombrillaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SombrillaMapper {

    public SombrillaResponseDTO toResponseDTO(SombrillaEntity entity) {
        if (entity == null) return null;

        SombrillaResponseDTO dto = new SombrillaResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        dto.setNumero(entity.getNumero());
        dto.setEtamanio(entity.getTamanio());

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

    public SombrillaEntity toEntity(SombrillaRequestDTO dto) {
        if (dto == null) return null;

        SombrillaEntity entity = new SombrillaEntity();
        entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
        entity.setTamanio(dto.getEtamanio());
        return entity;
    }

    public void actualizarDesdeRequest(SombrillaRequestDTO dto, SombrillaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        entity.setNumero(dto.getNumero());
        entity.setTamanio(dto.getEtamanio());
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