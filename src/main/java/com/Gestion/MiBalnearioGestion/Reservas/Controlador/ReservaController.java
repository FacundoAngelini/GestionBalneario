package com.Gestion.MiBalnearioGestion.Reservas.Controlador;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoReservaService;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final PagoReservaService pagoReservaService;
    private final ReservaServicio reservaServicio;

    @PostMapping("/checkout-online")
    public ResponseEntity<CheckoutResponseDTO> checkoutOnline(@Valid @RequestBody ReservaDTO dto) {
        CheckoutResponseDTO respuesta = reservaServicio.crearReservaYGenerarCheckout(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or @securityService.esElMismoCliente(authentication, #id)\"")
    public ResponseEntity<ReservaDTO> obtenerReservaEspecifica(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas() {
        return ResponseEntity.ok(reservaServicio.listarTodas());
    }

    @PutMapping("/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Void> cancelarReserva(@Valid @RequestBody CancelarReservaDTO dto) {
        reservaServicio.cancelarReservaConAnticipacion(dto);
        return ResponseEntity.noContent().build();
    }
}