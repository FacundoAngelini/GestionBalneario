package com.Gestion.MiBalnearioGestion.Clientes.Controller;

import com.Gestion.MiBalnearioGestion.Clientes.Service.ClienteGestionService;
import com.Gestion.MiBalnearioGestion.Clientes.dto.*;
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

@Tag(name = "Gestión de clientes", description = "Listado, ficha, alta, modificación, baja y reactivación de clientes por el personal")
@RestController
@RequestMapping("/api/v1/clientes/gestion")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
public class ClienteGestionController {

    private final ClienteGestionService servicio;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "El texto busca en nombre, apellido, DNI, email y teléfono. Devuelve hasta 200 resultados. Sin 'activo' trae todos.")
    public ResponseEntity<List<ClienteResponse>> listar(
            @RequestParam(required = false) String texto, @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(servicio.listar(texto, activo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ficha de un cliente", description = "Datos, usuario, reservas activas y resumen de las anteriores.")
    public ResponseEntity<ClienteFichaDTO> ficha(@PathVariable UUID id) {
        return ResponseEntity.ok(servicio.ficha(id));
    }

    @PostMapping
    @Operation(summary = "Dar de alta a un cliente en el mostrador", description = "Con usuario y contraseña provisoria, que se le entrega al cliente.")
    public ResponseEntity<ClienteFichaDTO> crear(@Valid @RequestBody ClienteAltaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar los datos de un cliente", description = "Incluye el DNI.")
    public ResponseEntity<ClienteFichaDTO> actualizar(@PathVariable UUID id, @Valid @RequestBody ClienteEdicionDTO dto) {
        return ResponseEntity.ok(servicio.actualizar(id, dto));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Dar de baja o reactivar a un cliente",
            description = "Pide motivo. La baja se rechaza si el cliente tiene reservas pendientes o confirmadas.")
    public ResponseEntity<ClienteFichaDTO> cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambioEstadoClienteDTO dto) {
        return ResponseEntity.ok(servicio.cambiarEstado(id, dto));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Historial de la ficha de un cliente")
    public ResponseEntity<List<EventoClienteDTO>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(servicio.historial(id));
    }
}