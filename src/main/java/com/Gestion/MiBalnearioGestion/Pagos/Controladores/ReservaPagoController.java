package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoReservaService;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaServicio;
import com.mercadopago.net.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/reservas-pagos")
@RequiredArgsConstructor
public class ReservaPagoController {

    private final PagoReservaService pagoReservaService;
    private final ReservaServicio reservaServicio;

    @PostMapping("/checkout-online")
    public ResponseEntity<CheckoutResponseDTO> iniciarReservaOnline(@Valid @RequestBody ReservaDTO dto) {
        CheckoutResponseDTO respuesta = reservaServicio.crearReservaYGenerarCheckout(dto);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/mostrador-efectivo")
    public ResponseEntity<Void> iniciarReservaPresencial(
            @Valid @RequestBody ReservaDTO dto,
            @RequestParam UUID empleadoPublicId) {

        pagoReservaService.procesarPagoEfectivoMostrador(dto, empleadoPublicId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}