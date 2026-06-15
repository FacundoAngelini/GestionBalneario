package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoLugarDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.DetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.PedidoService;
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

    private final PedidoService pedidoService;
    private final DetallePedidoService detallePedidoService;


    @PostMapping("/mesa")
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE', 'ADMIN')") //admin solo para probar
    public ResponseEntity<PedidoResponse> crearPedidoMesa(@RequestBody PedidoMesaDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoMesa(dto));
    }

    @PostMapping("/lugar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'MOZO', 'REPARTIDOR', 'GERENTE')")
    public ResponseEntity<PedidoResponse> crearPedidoLugar(@RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoLugarOnline(dto));
    }


    @PatchMapping("/{pedidoPublicId}/cancelar")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'CLIENTE')")
    public ResponseEntity<Void> cancelarPedido(@PathVariable UUID pedidoPublicId) {
        pedidoService.cancelarPedido(pedidoPublicId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/detalle/{detallePublicId}")
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE')")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable UUID detallePublicId) {
        detallePedidoService.eliminarDetalle(detallePublicId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE', 'COCINERO')")
    public ResponseEntity<List<PedidoResponse>> listarPedidos(
            @RequestParam(required = false) ETipoPedido tipo,
            @RequestParam(required = false) EEstadoPedido estado,
            @RequestParam(required = false) LocalDate fecha) {
        return ResponseEntity.ok(pedidoService.buscarTodosConFiltros(tipo, estado, fecha));
    }

    @GetMapping("/{pedidoPublicId}")
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE', 'COCINERO', 'CLIENTE')")
    public ResponseEntity<PedidoResponse> buscarPedido(@PathVariable UUID pedidoPublicId) {
        return ResponseEntity.ok(pedidoService.buscarPorPublicId(pedidoPublicId));
    }
}