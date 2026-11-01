package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ConfiguracionPagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.ConfiguracionPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Configuración de pagos", description = "Parámetros de cobro (descuento por pago en efectivo)")
@RestController
@RequestMapping("/api/v1/configuracion/pagos")
@RequiredArgsConstructor
public class ConfiguracionPagoController {

    private final ConfiguracionPagoService servicio;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    @Operation(summary = "Obtener la configuración de cobro")
    public ResponseEntity<ConfiguracionPagoDTO> obtener() {
        return ResponseEntity.ok(new ConfiguracionPagoDTO(servicio.obtener().getDescuentoEfectivo()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Definir el descuento por pago en efectivo", description = "Solo el administrador. Entre 0 y 50 %.")
    public ResponseEntity<ConfiguracionPagoDTO> actualizar(@Valid @RequestBody ConfiguracionPagoDTO dto) {
        return ResponseEntity.ok(servicio.actualizarDescuentoEfectivo(dto.descuentoEfectivo()));
    }
}