package com.Gestion.MiBalnearioGestion.Reservas.Controlador;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoReservaService;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaServicio;
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
public class ReservaController {

    private final ReservaServicio reservaServicio;

    @PostMapping("/checkout-online")
    public ResponseEntity<CheckoutResponseDTO> checkoutOnline(@Valid @RequestBody ReservaDTO dto) {
        CheckoutResponseDTO respuesta = reservaServicio.crearReservaYGenerarCheckout(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION') or @securityService.esDuenioDeLaReserva(#id)")
    public ResponseEntity<ReservaDTO> obtenerReservaEspecifica(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas(@RequestParam(required = false) EReservaEstado estado,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                                                    @RequestParam(required = false) UUID clientePublicId) {
        return ResponseEntity.ok(reservaServicio.listarReservasConFiltros(estado,fechaDesde,fechaHasta,clientePublicId));
    }

    @PutMapping("/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO') or @securityService.esElPropioCliente(#dto.clientePublicId)")
    public ResponseEntity<Void> cancelarReserva(@Valid @RequestBody CancelarReservaDTO dto) {
        reservaServicio.cancelarReservaConAnticipacion(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancelar-administrativo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<Void> cancelarReservaAdministrativa(@PathVariable UUID id) {
        reservaServicio.cancelarReservaPorPersonal(id);
        return ResponseEntity.noContent().build();
    }
}