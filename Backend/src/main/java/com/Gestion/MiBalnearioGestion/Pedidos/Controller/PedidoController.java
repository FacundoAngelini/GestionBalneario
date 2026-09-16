package com.Gestion.MiBalnearioGestion.Pedidos.Controller;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.DetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IDetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IPedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedido Controller", description = "Endpoints para la gestión del ciclo de vida de los pedidos, comanda gastronómica y lógica de cobranza/cancelación")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final IDetallePedidoService detallePedidoService;

    @PostMapping("/mesa")
    @PreAuthorize("hasAnyRole('MOZO', 'CAJERO', 'GERENTE', 'ADMIN')")
    @Operation(summary = "Crear pedido para una mesa", description = "Registra una comanda vinculada a una mesa física del salón gastronómico. Requiere rol de personal interno (MOZO, CAJERO, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido de mesa creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o mesa inexistente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<PedidoResponse> crearPedidoMesa(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoMesa(dto));
    }

    @PostMapping("/lugar/online")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'GERENTE')")
    @Operation(summary = "Crear pedido online desde un recurso", description = "Permite a un cliente autenticado autogestionar un pedido desde su ubicación (carpa/sombrilla) mediante la app.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido online registrado con éxito (Listo para pasarela de pago)"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o recurso no asignado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<PedidoResponse> crearPedidoLugarOnline(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoLugarOnline(dto));
    }

    @PostMapping("/lugar/presencial")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN', 'REPARTIDOR')")
    @Operation(summary = "Crear pedido presencial en mostrador para un recurso", description = "Registra un pedido tomado físicamente por el cajero para ser llevado a un recurso. Retorna un ticket de facturación rápida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido presencial creado con éxito. Retorna el ticket."),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<TicketDTO> crearPedidoLugarPresencial(@Valid @RequestBody PedidoLugarDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedidoLugarPresencial(dto));
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE', 'ADMINISTRACION', 'CAJERO','MOZO', 'REPARTIDOR') or @securityService.esDuenioDelPedido(#publicId)")
    @Operation(summary = "Buscar pedido por ID", description = "Recupera el estado y el detalle de un pedido. Accesible por el personal operativo o dinámicamente por el propio CLIENTE dueño del pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - No es dueño del pedido ni cuenta con privilegios operativos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<PedidoResponse> buscarPorId(
            @Parameter(description = "UUID público del pedido a consultar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID publicId) {
        return ResponseEntity.ok(pedidoService.buscarPorPublicId(publicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CAJERO','MOZO','REPARTIDOR')")
    @Operation(summary = "Listar y filtrar todos los pedidos", description = "Obtiene el listado general de comandas aplicando filtros por modalidad, estado del ciclo de vida o fecha específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de pedidos obtenido con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<PedidoResponse>> buscarTodos(
            @Parameter(description = "Filtrar por tipo/modalidad de entrega") @RequestParam(required = false) ETipoPedido tipoPedido,
            @Parameter(description = "Filtrar por estado del ciclo de vida del pedido") @RequestParam(required = false) EEstadoPedido estadoPedido,
            @Parameter(description = "Filtrar por fecha exacta de realización (Format: YYYY-MM-DD)", example = "2026-06-16") @RequestParam(required = false) LocalDate fecha) {
        return ResponseEntity.ok(pedidoService.buscarTodos(tipoPedido, estadoPedido, fecha));
    }

    @PostMapping("/{pedidoPublicId}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','MOZO') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    @Operation(summary = "Agregar un producto al pedido", description = "Añade una nueva línea de producto o incrementa cantidades en un pedido en curso. Accesible por personal de salón o el CLIENTE dueño.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto añadido con éxito al detalle"),
            @ApiResponse(responseCode = "400", description = "ID de producto inválido o cantidad errónea", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Restricción de seguridad", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<DetallePedidoResponse> agregarDetalle(
            @Parameter(description = "UUID público del pedido al que se le suma el ítem", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID pedidoPublicId,
            @Valid @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.ok(pedidoService.agregarDetalleAMesa(pedidoPublicId, request));
    }

    @DeleteMapping("/detalles/{detallePublicId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','MOZO')")
    @Operation(summary = "Eliminar un producto del pedido", description = "Remueve una línea de detalle completa de una comanda abierta a partir de su ID de detalle. Requiere rol operativo de salón.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Línea de detalle eliminada con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID de detalle erróneo", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Línea de detalle no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminarDetalle(
            @Parameter(description = "UUID público de la línea de detalle específica a remover", example = "c1b2a3f4-e5d6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID detallePublicId) {
        detallePedidoService.eliminarDetalle(detallePublicId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pedidoPublicId}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CAJERO', 'MOZO', 'REPARTIDOR') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    @Operation(summary = "Cancelar pedido estándar (No pagado)", description = "Da de baja y cancela un pedido que aún no fue transaccionado monetariamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido cancelado con éxito", content = @Content),
            @ApiResponse(responseCode = "400", description = "El pedido no se puede cancelar en su estado actual", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<Void> cancelarPedido(
            @Parameter(description = "UUID público del pedido a cancelar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID pedidoPublicId) {
        pedidoService.cancelarPedido(pedidoPublicId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pedidoPublicId}/cancelar-pagado")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN')")
    @Operation(summary = "Cancelar pedido pagado (Con reembolso)", description = "Operación crítica para cancelar un pedido abonado. Dispara la lógica interna para el proceso de reembolso de dinero al cliente. Requiere rol de supervisión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido cancelado y flujo de reembolso iniciado con éxito", content = @Content),
            @ApiResponse(responseCode = "400", description = "El pedido no registra pagos o ya fue entregado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol de administración/caja", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<Void> cancelarPedidoPagado(
            @Parameter(description = "UUID público del pedido pagado a cancelar", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID pedidoPublicId) {
        pedidoService.cancelarPedidoPagadoConReembolso(pedidoPublicId);
        return ResponseEntity.noContent().build();
    }
}