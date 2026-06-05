package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.TicketMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;

    private final iTicketRepository ticketRepository;

    public TicketDTO buscarPorPublicId(UUID publicId)
    {
        TicketEntity ticket = ticketRepository.findByPublicId(publicId)
                .orElseThrow(()->new EntidadNoEncontradaException("No existe un ticket con id: ",publicId.toString()));
        return ticketMapper.convertToDTO(ticket);
    }

    @Transactional
    public TicketDTO crear (TicketDTO dto)
    {
        if(ticketRepository.existsByPublicId(dto.getPublicId()))
        {
            throw new EntidadExistenteException("El ticket que intenta crear ya existe", dto.toString());
        }
        TicketEntity ticket = ticketMapper.convertToEntity(dto,TicketEntity.class);

        return ticketMapper.convertToDTO(ticketRepository.save(ticket));
    }

    /** NO SABRIA DECIRTE SI SE DEBERIA PODER ACTUALIZAR IMAGINO QUE NO POR ESO LO BORRO
     @Transaccional
    public TicketDTO actualizar(UUID publicId,TicketDTO dto)
    {
       TicketEntity ticket = ticketMapper.convertToEntity(buscarPorPublicId(publicId),TicketEntity.class);
       ticketMapper.updateEntityFromDTO(dto,ticket);
        return ticketMapper.convertToDTO(ticketRepository.save(ticket));
    } */


}
