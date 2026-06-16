package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.ITicketService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;


    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO', 'CAJERO')")
    public ResponseEntity<TicketDTO> obtenerPorPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(ticketService.buscarPorPublicId(publicId));
    }

    @GetMapping("/pago/{pagoPublicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<TicketDTO> obtenerTicketDeUnPago(@PathVariable UUID pagoPublicId) {
        return ResponseEntity.ok(ticketService.ticketDeUnPago(pagoPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<List<TicketDTO>> listarTicketsFiltrados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) UUID empleadoPublicId
    ) {
        List<TicketDTO> tickets = ticketService.listarTicketsConFiltros(fechaDesde, fechaHasta, empleadoPublicId);
        return ResponseEntity.ok(tickets);
    }
}