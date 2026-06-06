package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.TicketMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final iPagoRepository pagoRepository;

    private final PagoMapper pagoMapper;

    private final TicketService ticketService;

    private final iTicketRepository ticketRepository;

    private final TicketMapper ticketMapper;


    public PagoDTO buscarPorPublicId (UUID publicId){
        PagoEntity pago = pagoRepository.findByPublicId(publicId)
                .orElseThrow(()-> new EntidadNoEncontradaException("No existe un pago con id: ",publicId.toString()));
        return pagoMapper.convertToDTO(pago);
    }

    //En el create de Pago se usa el create de Ticket logicamente no hay Controller de ticket
    // se accede desde pagos en mi opinion
    public PagoDTO crear (PagoDTO dto) {

        if (pagoRepository.existsByPublicId(dto.getPublicId())) {
            throw new EntidadExistenteException("Ya existe un pago con id: ", dto.getPublicId().toString());
        }
        PagoEntity pago = pagoMapper.convertToEntity(dto, PagoEntity.class);
        if (pago.getEestadoPago() != EestadoPago.PAGADO)
        {
            // Throw de Excepcion o re solicitar pago
        }

        //El ticket se crea a partir de que se confirme el pago
        TicketEntity ticket = ticketMapper.convertToEntity(ticketService.generarTicket(dto.getTicketDTO()),TicketEntity.class);
        pago.setTicket(ticket);

        ticket.setPagoEntity(pago);
        ticketRepository.save(ticket);

        return pagoMapper.convertToDTO(pagoRepository.save(pago));
    }

    @Transactional
    public void borrar (UUID publicId){
        pagoRepository.delete(pagoRepository.findByPublicId(publicId)
                .orElseThrow(()-> new EntidadNoEncontradaException("No existe un Pago con id: ", publicId.toString())));
    }

    @Transactional
    public PagoDTO confirmarEstadoPago (UUID publicId){


        return null;
    }


}
