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
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/mesa")
    public ResponseEntity<PedidoResponse> crearPedidoMesa(@RequestBody @Valid PedidoMesaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedidoMesa(dto));
    }

    @PostMapping("/reserva")
    public ResponseEntity<PedidoResponse> crearPedidoReserva(@RequestBody @Valid PedidoReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedidoReserva(dto));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable UUID publicId) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(publicId));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PedidoResponse>> obtenerPorTipo(@PathVariable ETipoPedido tipo) {
        return ResponseEntity.ok(pedidoService.obtenerPorTipo(tipo));
    }


    ////si queres hacer uno con un pedido existente y argegarle pero yo no lo haria
    //@PostMapping("/{publicId}/productos")
    //public ResponseEntity<PedidoResponse> agregarProductos(
    //        @PathVariable UUID publicId,
    //        @RequestBody @Valid List<DetallePedidoRequest> detalles) {
    //    return ResponseEntity.ok(pedidoService.agregarProductos(publicId, detalles));
    //}
}
