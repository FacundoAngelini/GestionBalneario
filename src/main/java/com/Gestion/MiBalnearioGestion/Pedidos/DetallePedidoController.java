package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/detalles")
@RequiredArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    // No tiene POST propio — se crea desde /pedidos - fran

    @GetMapping("/pedido/{pedidoPublicId}")
    public ResponseEntity<List<DetallePedidoResponse>> obtenerPorPedido(@PathVariable UUID pedidoPublicId) {
        return ResponseEntity.ok(detallePedidoService.obtenerDetallesPorPedido(pedidoPublicId));
    }

   //@PatchMapping("/{publicId}/cantidad")
   //public ResponseEntity<DetallePedidoResponse> actualizarCantidad(
   //        @PathVariable UUID publicId,
   //        @RequestParam int cantidad) {
   //    return ResponseEntity.ok(detallePedidoService.actualizarCantidad(publicId, cantidad));
   //}

}