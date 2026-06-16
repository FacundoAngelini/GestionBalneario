package com.Gestion.MiBalnearioGestion.Pedidos.Controller;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.DetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IDetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IPedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;
    private final IDetallePedidoService detallePedidoService;

    @PostMapping("/mesa")
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE', 'ADMIN')")
    public ResponseEntity<PedidoResponse> crearPedidoMesa(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoMesa(dto));
    }

    @PostMapping("/lugar/online")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'GERENTE')")
    public ResponseEntity<PedidoResponse> crearPedidoLugarOnline(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoLugarOnline(dto));
    }

    @PostMapping("/lugar/presencial")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN', 'REPARTIDOR')")
    public ResponseEntity<TicketDTO> crearPedidoLugarPresencial(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoLugarPresencial(dto));
    }


    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE', 'ADMINISTRACION', 'CAJERO','MOZO', 'REPARTIDOR') or @securityService.esDuenioDelPedido(#publicId)")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(pedidoService.buscarPorPublicId(publicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CAJERO','MOZO','REPARTIDOR')")
    public ResponseEntity<List<PedidoResponse>> buscarTodos(@RequestParam(required = false) ETipoPedido tipoPedido,
                                                            @RequestParam(required = false) EEstadoPedido estadoPedido,
                                                            @RequestParam(required = false) LocalDate fecha) {
        return ResponseEntity.ok(pedidoService.buscarTodos(tipoPedido, estadoPedido, fecha));
    }

    @PostMapping("/{pedidoPublicId}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','MOZO') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    public ResponseEntity<DetallePedidoResponse> agregarDetalle(@PathVariable UUID pedidoPublicId,
                                                                @Valid @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.ok(pedidoService.agregarDetalleAMesa(pedidoPublicId, request));
    }

    @DeleteMapping("/detalles/{detallePublicId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','MOZO')")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable UUID detallePublicId) {
        detallePedidoService.eliminarDetalle(detallePublicId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pedidoPublicId}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CAJERO', 'MOZO', 'REPARTIDOR') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    public ResponseEntity<Void> cancelarPedido(@PathVariable UUID pedidoPublicId) {
        pedidoService.cancelarPedido(pedidoPublicId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pedidoPublicId}/cancelar-pagado")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN')")
    public ResponseEntity<Void> cancelarPedidoPagado(@PathVariable UUID pedidoPublicId) {
        pedidoService.cancelarPedidoPagadoConReembolso(pedidoPublicId);
        return ResponseEntity.noContent().build();
    }
}