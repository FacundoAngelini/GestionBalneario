package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.ITicketService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ticket Controller", description = "Endpoints para la consulta, auditoría y control de comprobantes comerciales (Tickets) emitidos en el sistema")
public class TicketController {

    private final ITicketService ticketService;

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO', 'CAJERO')")
    @Operation(summary = "Buscar ticket por ID público", description = "Recupera un comprobante fiscal o ticket comercial específico a partir de su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado y recuperado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios operativos de caja requeridos", content = @Content),
            @ApiResponse(responseCode = "404", description = "El ticket solicitado no existe", content = @Content)
    })
    public ResponseEntity<TicketDTO> obtenerPorPublicId(
            @Parameter(description = "UUID público del ticket a consultar", example = "9f8e7d6c-5b4a-3f2e-1d0c-b1a09f8e7d6c")
            @PathVariable UUID publicId) {
        return ResponseEntity.ok(ticketService.buscarPorPublicId(publicId));
    }

    @GetMapping("/pago/{pagoPublicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener ticket asociado a un pago", description = "Permite a la administración recuperar el comprobante comercial emitido que corresponde a una transacción financiera específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket asociado al pago recuperado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID del pago inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol administrativo jerárquico", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún ticket vinculado al pago provisto", content = @Content)
    })
    public ResponseEntity<TicketDTO> obtenerTicketDeUnPago(
            @Parameter(description = "UUID público del pago para el cual se busca el ticket", example = "5f4e3d2c-b1a0-9e8d-7c6b-5a4f3e2d1c0b")
            @PathVariable UUID pagoPublicId) {
        return ResponseEntity.ok(ticketService.ticketDeUnPago(pagoPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y filtrar tickets emitidos", description = "Herramienta de auditoría para la administración del balneario/restaurante. Filtra tickets por rango de fecha y hora exacta, o por la actividad de facturación de un empleado en particular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets filtrados obtenido con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes", content = @Content)
    })
    public ResponseEntity<List<TicketDTO>> listarTicketsFiltrados(
            @Parameter(description = "Fecha y hora inicial del rango de búsqueda (Format ISO: YYYY-MM-DDTHH:MM:SS)", example = "2026-06-16T08:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,

            @Parameter(description = "Fecha y hora final del rango de búsqueda (Format ISO: YYYY-MM-DDTHH:MM:SS)", example = "2026-06-16T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,

            @Parameter(description = "UUID público del empleado (cajero/mozo) que emitió los comprobantes", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @RequestParam(required = false) UUID empleadoPublicId
    ) {
        List<TicketDTO> tickets = ticketService.listarTicketsConFiltros(fechaDesde, fechaHasta, empleadoPublicId);
        return ResponseEntity.ok(tickets);
    }
}