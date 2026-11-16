package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IReservaCobroServicio;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.EventoReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaAuditoriaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cobros de reservas", description = "Registro y anulación de cobros, comprobante, historial y reserva presencial")
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaCobroController {

    private static final String PERSONAL = "hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')";

    private final IReservaCobroServicio cobroServicio;
    private final ReservaAuditoriaServicio auditoria;

    @PostMapping("/presencial")
    @PreAuthorize(PERSONAL)
    @Operation(summary = "Crear una reserva en el mostrador", description = "Crea la reserva a nombre de un cliente ya registrado. Con cobrarAhora=true registra además el cobro en efectivo.")
    public ResponseEntity<ReservaPresencialResponseDTO> crearPresencial(@Valid @RequestBody ReservaPresencialDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cobroServicio.crearPresencial(dto));
    }

    @PostMapping("/{id}/cobro")
    @PreAuthorize(PERSONAL)
    @Operation(summary = "Registrar el cobro de una reserva", description = "Quien cobra se toma del usuario logueado. Si la reserva estaba pendiente, queda confirmada.")
    public ResponseEntity<CobroComprobanteDTO> registrarCobro(
            @PathVariable UUID id,
            @RequestBody(required = false) CobroRequestDTO body) {
        MetodoPago metodo = body == null || body.metodoPago() == null ? MetodoPago.EFECTIVO : body.metodoPago();
        return ResponseEntity.ok(cobroServicio.registrarCobro(id, metodo));
    }

    @GetMapping("/{id}/cobro")
    @PreAuthorize(PERSONAL + " or @securityService.esDuenioDeLaReserva(#id)")
    @Operation(summary = "Obtener el comprobante del cobro vigente de una reserva")
    public ResponseEntity<CobroComprobanteDTO> comprobante(@PathVariable UUID id) {
        return ResponseEntity.ok(cobroServicio.obtenerComprobante(id));
    }

    @PutMapping("/{id}/cobro/anular")
    @PreAuthorize(PERSONAL)
    @Operation(summary = "Anular el cobro de una reserva", description = "El motivo es obligatorio y queda registrado junto con quién lo anuló.")
    public ResponseEntity<ReservaDTO> anularCobro(@PathVariable UUID id, @Valid @RequestBody AnularCobroDTO body) {
        return ResponseEntity.ok(cobroServicio.anularCobro(id, body.motivo()));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize(PERSONAL)
    @Operation(summary = "Historial de acciones de una reserva")
    public ResponseEntity<List<EventoReservaDTO>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(auditoria.historial(id));
    }
}