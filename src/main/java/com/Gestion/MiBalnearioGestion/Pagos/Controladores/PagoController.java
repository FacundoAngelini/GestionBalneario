package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoPedidoMesaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoPedidoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoPedidoMesaService pedidoMesaService;
    private final PagoPedidoReservaService pedidoReservaService;
    private final PagoService  pagoService;

    @PostMapping("/pedido-mesa")
    public ResponseEntity<String> pagarPedidoMesa(@Valid @RequestBody PagoPedidoMesaDTO dto) {
        String urlMercadoPago = pedidoMesaService.iniciarPago(dto);
        return ResponseEntity.ok(urlMercadoPago);
    }

    @PostMapping("/pedido-carpa")
    public ResponseEntity<String> pagarPedidoCarpa(@Valid @RequestBody PagoPedidoReservaDTO dto) {
        String urlMercadoPago = pedidoReservaService.iniciarPago(dto);
        return ResponseEntity.ok(urlMercadoPago);
    }
    @GetMapping("/{reservaPublicId}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PagoReservaResponseDTO> obtenerPagoPorReserva(@PathVariable UUID reservaPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorReserva(reservaPublicId));
    }
}