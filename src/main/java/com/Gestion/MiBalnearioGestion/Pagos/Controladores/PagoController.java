package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoPedidoGastronomicoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoPedidoGastronomicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final IPagoService pagoService;
    private final IPagoPedidoGastronomicoService pagoPedidoGastronomicoService;

    @PostMapping("/presencial")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN')")
    public ResponseEntity<TicketDTO> pagarPresencial(@Valid @RequestBody PagoPresencialRequest request) {
        return ResponseEntity.ok(pagoPedidoGastronomicoService.procesarPagoPresencial(request.pedidoPublicId(), request.empleadoPublicId(), request.metodoPago()));
    }


    @GetMapping("/pedido/{pedidoPublicId}")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'CLIENTE', 'MOZO') " + "or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    public ResponseEntity<PagoDTO> obtenerPagoPorPedido(@PathVariable UUID pedidoPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(pedidoPublicId));
    }

    @GetMapping("/{reservaPublicId}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO') " + "or @securityService.esDuenioDeLaReserva(#reservaPublicId)")
    public ResponseEntity<PagoReservaResponseDTO> obtenerPagoPorReserva(@PathVariable UUID reservaPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorReserva(reservaPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<List<PagoResponseDTO>> listarPagosFiltrados(@RequestParam(required = false) EestadoPago estado,
                                                                      @RequestParam(required = false) MetodoPago metodo,
                                                                      @RequestParam(required = false) Double montoMin,
                                                                      @RequestParam(required = false) Double montoMax,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(pagoService.buscarPagosConFiltros(
                estado, metodo, montoMin, montoMax, fechaDesde, fechaHasta));
    }
}