package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoPedidoMesaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
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

    private final IPagoPedidoMesaService pedidoMesaService;
    private final IPagoService pagoService;


    @PostMapping("/pedido-mesa")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN', 'MOZO')")
    public ResponseEntity<String> pagarPedidoMesa(@Valid @RequestBody PagoPedidoMesaDTO dto) {
        String urlMercadoPago = pedidoMesaService.iniciarPago(dto);
        return ResponseEntity.ok(urlMercadoPago);
    }
    @GetMapping("/pedido/{pedidoPublicId}")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'CLIENTE', 'MOZO') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    public ResponseEntity<PagoDTO> obtenerPagoPorPedido(@PathVariable UUID pedidoPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(pedidoPublicId));
    }

    @GetMapping("/{reservaPublicId}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO') or @securityService.esDuenioDeLaReserva(#reservaPublicId)")
    public ResponseEntity<PagoReservaResponseDTO> obtenerPagoPorReserva(@PathVariable UUID reservaPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorReserva(reservaPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<List<PagoReservaResponseDTO>> listarPagosFiltrados(
            @RequestParam(required = false) EestadoPago estado,
            @RequestParam(required = false) MetodoPago metodo,
            @RequestParam(required = false) Double montoMin,
            @RequestParam(required = false) Double montoMax,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(pagoService.buscarPagosConFiltros(
                estado, metodo, montoMin, montoMax, fechaDesde, fechaHasta));
    }
}