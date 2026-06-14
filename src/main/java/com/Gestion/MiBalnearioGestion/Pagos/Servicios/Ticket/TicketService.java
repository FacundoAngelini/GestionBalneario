package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Ticket;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.TicketMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.ITicketService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Specification.TicketSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService implements ITicketService {

    private final TicketMapper ticketMapper;
    private final iTicketRepository ticketRepository;
    private final iPagoRepository pagoRepository;

    @Transactional(readOnly = true)
    @Override
    public TicketDTO ticketDeUnPago(UUID publicId_pago) {
        TicketEntity ticket = pagoRepository.findByPublicId(publicId_pago)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe un pago con id: ", publicId_pago.toString()))
                .getTicket();

        if (ticket == null) {
            throw new EntidadNoEncontradaException("El pago especificado aún no posee un ticket generado.", publicId_pago.toString());
        }
        return ticketMapper.convertToDTO(ticket);
    }

    @Transactional(readOnly = true)
    @Override
    public TicketDTO buscarPorPublicId(UUID publicId) {
        TicketEntity ticket = ticketRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el ticket con el ID especificado", publicId.toString()));
        return ticketMapper.convertToDTO(ticket);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketDTO> listarTicketsConFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta, UUID empleadoPublicId) {
        PredicateSpecification<TicketEntity> spec = PredicateSpecification.allOf(
                TicketSpecification.fechaDesde(fechaDesde),
                TicketSpecification.fechaHasta(fechaHasta),
                TicketSpecification.empleadoIgual(empleadoPublicId)
        );

        return ticketRepository.findAll(spec).stream()
                .map(ticketMapper::convertToDTO)
                .toList();
    }

    @Transactional
    public TicketDTO generarTicket(TicketDTO dto) {
        if (ticketRepository.existsByPublicId(dto.getPublicId())) {
            throw new EntidadExistenteException("El ticket que intenta crear ya existe", dto.toString());
        }
        TicketEntity ticket = ticketMapper.convertToEntity(dto, TicketEntity.class);
        return ticketMapper.convertToDTO(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketDTO enviarTicketXMail(TicketDTO dto) {
        // Tu futura lógica de mensajería
        return null;
    }
}
