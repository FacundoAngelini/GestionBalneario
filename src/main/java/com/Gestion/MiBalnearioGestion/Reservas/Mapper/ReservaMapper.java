package com.Gestion.MiBalnearioGestion.Reservas.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReservaMapper {

    public ReservaDTO convertToDTO(ReservaEntity entity) {
        if (entity == null) return null;

        ReservaDTO dto = new ReservaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setEstadoReserva(entity.getEstadoReserva());

        if (entity.getCliente() != null) {
            dto.setClientePublicId(entity.getCliente().getPublicId());
        }

        if (entity.getRecursos() != null) {
            dto.setRecursosPublicIds(entity.getRecursos().stream()
                    .map(RecursoEntity::getPublicId)
                    .toList());
        }

        return dto;
    }
}