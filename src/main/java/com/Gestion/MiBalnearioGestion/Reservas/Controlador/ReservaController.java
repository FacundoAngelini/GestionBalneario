package com.Gestion.MiBalnearioGestion.Reservas.Controlador;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoReservaService;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.IReservaServicio;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaServicio;
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
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reserva Controller", description = "Endpoints para la gestión, consulta, checkout y cancelación de reservas")
public class ReservaController {

    private final IReservaServicio reservaServicio;
    @PostMapping("/checkout-online")
    @Operation(summary = "Procesar checkout online de una reserva",
            description = "Registra una reserva de forma preliminar y genera la pasarela de pago correspondiente (Mercado Pago).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout e intención de pago generados con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de la reserva inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - Los recursos ya se encuentran reservados para las fechas seleccionadas", content = @Content)
    })
    public ResponseEntity<CheckoutResponseDTO> checkoutOnline(@Valid @RequestBody ReservaDTO dto) {
        CheckoutResponseDTO respuesta = reservaServicio.crearReservaYGenerarCheckout(dto);
        return ResponseEntity.ok(respuesta);
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION') or @securityService.esDuenioDeLaReserva(#id)")
    @Operation(summary = "Obtener una reserva específica por ID",
            description = "Permite recuperar los datos de una reserva mediante su UUID público. Accesible por personal autorizado o por el dueño de la reserva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada y devuelta con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - No tiene permisos sobre esta reserva", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    public ResponseEntity<ReservaDTO> obtenerReservaEspecifica(
            @Parameter(description = "UUID público de la reserva a consultar", example = "d3b07384-d113-4c4e-9c8e-cfbd6c4e3012")
            @PathVariable UUID id) {
        return ResponseEntity.ok(reservaServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    @Operation(summary = "Listar y filtrar reservas",
            description = "Obtiene una lista de reservas que pueden filtrarse de manera opcional por estado, rango de fechas y cliente. Requiere rol ADMIN, ADMINISTRADOR o GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reservas obtenido con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN, ADMINISTRACION o GERENTE", content = @Content)
    })
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas(
            @Parameter(description = "Filtrar por estado actual de la reserva")
            @RequestParam(required = false) EReservaEstado estado,

            @Parameter(description = "Filtrar reservas desde esta fecha de inicio (Formato ISO YYYY-MM-DD)", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,

            @Parameter(description = "Filtrar reservas hasta esta fecha de fin (Formato ISO YYYY-MM-DD)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,

            @Parameter(description = "Filtrar por el UUID público del cliente asociado", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam(required = false) UUID clientePublicId) {
        return ResponseEntity.ok(reservaServicio.listarReservasConFiltros(estado, fechaDesde, fechaHasta, clientePublicId));
    }


    @PutMapping("/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO') or @securityService.esElPropioCliente(#dto.clientePublicId)")
    @Operation(summary = "Cancelar una reserva (Usuario/Cliente)",
            description = "Solicita la cancelación de una reserva validando políticas de anticipación. Puede ser invocado por personal interno o por el propio cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada con éxito, no devuelve contenido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud o fuera del plazo permitido para cancelación", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - No tiene permiso para cancelar las reservas de este cliente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva o Cliente no encontrados", content = @Content)
    })
    public ResponseEntity<Void> cancelarReserva(@Valid @RequestBody CancelarReservaDTO dto) {
        reservaServicio.cancelarReservaConAnticipacion(dto);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/cancelar-administrativo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    @Operation(summary = "Cancelar una reserva por la vía administrativa",
            description = "Fuerza la cancelación de una reserva por parte del personal del sistema, omitiendo ciertas restricciones aplicadas al cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada por el área administrativa con éxito", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol administrativo (ADMIN, GERENTE, ADMINISTRATIVO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    public ResponseEntity<Void> cancelarReservaAdministrativa(
            @Parameter(description = "UUID público de la reserva a cancelar administrativamente", example = "d3b07384-d113-4c4e-9c8e-cfbd6c4e3012")
            @PathVariable UUID id) {
        reservaServicio.cancelarReservaPorPersonal(id);
        return ResponseEntity.noContent().build();
    }
}
