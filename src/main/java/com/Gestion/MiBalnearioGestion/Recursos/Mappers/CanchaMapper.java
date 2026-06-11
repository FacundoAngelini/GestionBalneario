package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CanchaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CanchaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class CanchaMapper {

    public CanchaResponseDTO toResponseDTO(CanchaEntity entity) {
        if (entity == null) return null;

        CanchaResponseDTO dto = new CanchaResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        dto.setTipoCancha(entity.getTipoCancha());
        dto.setCapacidad(entity.getCapacidad());
        dto.setIluminacion(entity.isIluminacion());

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

    public CanchaEntity toEntity(CanchaRequestDTO dto) {
        if (dto == null) return null;

        CanchaEntity entity = new CanchaEntity();
        entity.setNombre(dto.getNombre());
        entity.setTipoCancha(dto.getTipoCancha());
        entity.setCapacidad(dto.getCapacidad());
        entity.setIluminacion(dto.isIluminacion());
        return entity;
    }

    public void actualizarDesdeRequest(CanchaRequestDTO dto, CanchaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        entity.setTipoCancha(dto.getTipoCancha());
        entity.setCapacidad(dto.getCapacidad());
        entity.setIluminacion(dto.isIluminacion());
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
