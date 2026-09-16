package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoPedidoGastronomicoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoPedidoGastronomicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pago Controller", description = "Endpoints para el procesamiento de cobros presenciales, auditoría financiera y consultas transaccionales de caja")
public class PagoController {

    private final IPagoService pagoService;
    private final IPagoPedidoGastronomicoService pagoPedidoGastronomicoService;

    @PostMapping("/presencial")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'ADMIN')")
    @Operation(summary = "Procesar pago presencial en caja", description = "Registra y cierra la transacción económica de un pedido de forma física en el mostrador del establecimiento. Emite un ticket comercial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago procesado con éxito. Se retorna el ticket emitido."),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos, pedido ya pagado o empleado inexistente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol de caja requerido", content = @Content),
            @ApiResponse(responseCode = "442", description = "Error en la consistencia de montos o stock", content = @Content) // Ejemplo de error de negocio si correspondiera
    })
    public ResponseEntity<TicketDTO> pagarPresencial(@Valid @RequestBody PagoPresencialRequest request) {
        return ResponseEntity.ok(pagoPedidoGastronomicoService.procesarPagoPresencial(
                request.pedidoPublicId(), request.empleadoPublicId(), request.metodoPago()));
    }

    @GetMapping("/pedido/{pedidoPublicId}")
    @PreAuthorize("hasAnyRole('CAJERO', 'GERENTE', 'CLIENTE', 'MOZO') or @securityService.esDuenioDelPedido(#pedidoPublicId)")
    @Operation(summary = "Obtener pago por ID de pedido", description = "Recupera la información del pago imputado a una comanda específica. Permitido para staff interno o de forma dinámica para el CLIENTE dueño del pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del pago recuperada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Restricción de seguridad por propiedad de recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se registró ningún pago para el pedido indicado", content = @Content)
    })
    public ResponseEntity<PagoDTO> obtenerPagoPorPedido(
            @Parameter(description = "UUID público del pedido asociado al pago", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID pedidoPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(pedidoPublicId));
    }

    @GetMapping("/{reservaPublicId}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO') or @securityService.esDuenioDeLaReserva(#reservaPublicId)")
    @Operation(summary = "Obtener pago por ID de reserva", description = "Recupera el comprobante de pago vinculado al alquiler de un recurso (como carpas o sombrillas). Permitido para staff administrativo o el CLIENTE dueño de la reserva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del pago de la reserva obtenida con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se registran pagos para la reserva indicada", content = @Content)
    })
    public ResponseEntity<PagoReservaResponseDTO> obtenerPagoPorReserva(
            @Parameter(description = "UUID público de la reserva de alquiler", example = "7a8b9c0d-1e2f-3a4b-5c6d-e5f6d7c8b9a0")
            @PathVariable UUID reservaPublicId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorReserva(reservaPublicId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Listar y auditar pagos con filtros avanzados", description = "Herramienta de auditoría para la administración. Permite listar todas las transacciones históricas filtrando por rangos de fechas, montos mínimos/máximos, canales de pago y estados financieros.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de pagos filtrados obtenido con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol jerárquico o administrativo", content = @Content)
    })
    public ResponseEntity<List<PagoResponseDTO>> listarPagosFiltrados(
            @Parameter(description = "Filtrar por estado de la transacción") @RequestParam(required = false) EestadoPago estado,
            @Parameter(description = "Filtrar por canal o medio de cobro") @RequestParam(required = false) MetodoPago metodo,
            @Parameter(description = "Monto neto mínimo del rango de búsqueda", example = "5000.0") @RequestParam(required = false) Double montoMin,
            @Parameter(description = "Monto neto máximo del rango de búsqueda", example = "75000.50") @RequestParam(required = false) Double montoMax,
            @Parameter(description = "Fecha inicial del rango (Format: YYYY-MM-DD)", example = "2026-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(description = "Fecha final del rango (Format: YYYY-MM-DD)", example = "2026-06-16") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(pagoService.buscarPagosConFiltros(
                estado, metodo, montoMin, montoMax, fechaDesde, fechaHasta));
    }
}