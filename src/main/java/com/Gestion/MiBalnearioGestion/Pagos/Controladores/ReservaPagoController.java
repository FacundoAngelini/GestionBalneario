package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoReservaService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoService;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.mercadopago.net.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/pagos-reservas")
@RequiredArgsConstructor
public class ReservaPagoController {

    private final IPagoReservaService pagoReservaService;
    private final IPagoService pagoService;

    @PostMapping("/efectivo/empleado/{empleadoPublicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    public ResponseEntity<PagoReservaResponseDTO> iniciarReservaPresencial(@Valid @RequestBody ReservaDTO dto, @PathVariable UUID empleadoPublicId) {

        return ResponseEntity.ok(pagoReservaService.procesarPagoEfectivoMostrador(dto, empleadoPublicId));
    }
    @PutMapping("/{reservaPublicId}/cancelar-pago")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<Void> cancelarPagoReserva(@PathVariable UUID reservaPublicId) {
        pagoService.cancelarPagoYReserva(reservaPublicId);
        return ResponseEntity.noContent().build();
    }
}