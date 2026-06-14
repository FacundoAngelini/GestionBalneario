package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.TicketMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
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

    private final iPagoRepository pagoRepository;

    public TicketDTO ticketDeUnPago(UUID publicId_pago)
    {
        TicketEntity ticket = pagoRepository.findByPublicId(publicId_pago)
                .orElseThrow(()->new EntidadNoEncontradaException("No existe un pago con id: ",publicId_pago.toString()))
                .getTicket();
        return ticketMapper.convertToDTO(ticket);
    }

    @Transactional
    public TicketDTO generarTicket (TicketDTO dto)
    {
        if(ticketRepository.existsByPublicId(dto.getPublicId()))
        {
            throw new EntidadExistenteException("El ticket que intenta crear ya existe", dto.toString());
        }
        TicketEntity ticket = ticketMapper.convertToEntity(dto,TicketEntity.class);

        return ticketMapper.convertToDTO(ticketRepository.save(ticket));
    }


}
