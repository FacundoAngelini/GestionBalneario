package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoService;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.mercadopago.net.HttpStatus;
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

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/pagos-reservas")
@RequiredArgsConstructor
@Tag(name = "Reserva Pago Controller", description = "Endpoints administrativos para la gestión financiera de reservas de alquiler (carpas, sombrillas, cocheras) y anulaciones de transacciones")
public class ReservaPagoController {

    private final IPagoReservaService pagoReservaService;
    private final IPagoService pagoService;

    @PostMapping("/efectivo/empleado/{empleadoPublicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    @Operation(summary = "Registrar pago de reserva en efectivo", description = "Procesa el alta de una reserva de alquiler cobrada en efectivo en la oficina o mostrador del establecimiento. Genera el comprobante de pago vinculado de forma inmediata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago en efectivo registrado y reserva dada de alta con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de la reserva inválidos o empleado inexistente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol de administración o superior", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - El recurso ya se encuentra reservado para las fechas seleccionadas", content = @Content)
    })
    public ResponseEntity<PagoReservaResponseDTO> iniciarReservaPresencial(
            @Valid @RequestBody ReservaDTO dto,
            @Parameter(description = "UUID público del empleado administrativo o cajero que recibe el dinero", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @PathVariable UUID empleadoPublicId) {
        return ResponseEntity.ok(pagoReservaService.procesarPagoEfectivoMostrador(dto, empleadoPublicId));
    }

    @PutMapping("/{reservaPublicId}/cancelar-pago")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Anular pago y cancelar reserva", description = "Operación destructiva de auditoría. Cancela la reserva de alquiler activa y revierte/anula el estado del pago asociado en el módulo contable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pago y reserva anulados con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "400", description = "La reserva no se puede cancelar en su estado actual o no posee pagos asociados", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    public ResponseEntity<Void> cancelarPagoReserva(
            @Parameter(description = "UUID público de la reserva que se desea dar de baja junto a su pago", example = "7a8b9c0d-1e2f-3a4b-5c6d-e5f6d7c8b9a0")
            @PathVariable UUID reservaPublicId) {
        pagoService.cancelarPagoYReserva(reservaPublicId);
        return ResponseEntity.noContent().build();
    }
}