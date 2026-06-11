package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PrecioRequestRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrecioRecursoMapper {

    private final ModelMapper modelMapper;

    public PrecioRecursoResponseDTO toResponseDTO(PrecioRecursoEntity entity) {
        PrecioRecursoResponseDTO dto = new PrecioRecursoResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setPrecio(entity.getPrecio());
        dto.setFechaVigencia(entity.getFechaVigencia());
        dto.setFechaCaducada(entity.getFechaCaducada());
        dto.setRecursoPublicId(entity.getRecurso().getPublicId());
        return dto;
    }

    // ✅ Recibe el RequestDTO, no el ResponseDTO
    public PrecioRecursoEntity toEntity(PrecioRequestRecursoDTO dto) {
        PrecioRecursoEntity entity = new PrecioRecursoEntity();
        entity.setPrecio(dto.getPrecio());
        entity.setFechaVigencia(dto.getFechaVigencia());
        entity.setFechaCaducada(dto.getFechaCaducada());
        // El recurso se setea en el servicio, nunca acá
        return entity;
    }

    public void actualizarDesdeRequest(PrecioRequestRecursoDTO dto, PrecioRecursoEntity entity) {
        if (dto.getPrecio() > 0)           entity.setPrecio(dto.getPrecio());
        if (dto.getFechaVigencia() != null) entity.setFechaVigencia(dto.getFechaVigencia());
        if (dto.getFechaCaducada() != null) entity.setFechaCaducada(dto.getFechaCaducada());
    }
}