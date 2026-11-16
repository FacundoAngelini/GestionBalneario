package com.Gestion.MiBalnearioGestion.Empleados.Controller;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.*;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Servicio.IEmpleadoService;
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

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(name = "Empleado Controller", description = "Alta, consulta, modificación y cambios de estado de empleados")
public class EmpleadoController {

    private static final String GESTION = "hasAnyRole('ADMIN', 'GERENTE')";

    private final IEmpleadoService empleadoService;

    @GetMapping
    @PreAuthorize(GESTION)
    @Operation(summary = "Listar empleados", description = "Sin filtro trae todos los estados. El gerente no ve a los administradores.")
    public ResponseEntity<List<EmpleadoFichaDTO>> listar(@RequestParam(required = false) EEstadoEmpleado estado) {
        return ResponseEntity.ok(empleadoService.listarEmpleados(estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize(GESTION + " or @securityService.esElPropioEmpleado(#id)")
    @Operation(summary = "Ficha de un empleado", description = "El propio empleado ve su ficha sin el sueldo.")
    public ResponseEntity<EmpleadoFichaDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empleadoService.buscarPorIDpublico(id));
    }

    @PostMapping
    @PreAuthorize(GESTION)
    @Operation(summary = "Dar de alta a un empleado", description = "El administrador crea cualquier rol; el gerente, solo los de menor rango.")
    public ResponseEntity<EmpleadoFichaDTO> crear(@Valid @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crearEmpleado(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize(GESTION)
    @Operation(summary = "Modificar los datos de un empleado", description = "Solo de rango inferior. El rol no se cambia.")
    public ResponseEntity<EmpleadoFichaDTO> actualizar(@PathVariable UUID id, @Valid @RequestBody EmpleadoUpdateDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, dto));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize(GESTION)
    @Operation(summary = "Cambiar el estado de un empleado",
            description = "Suspender, dar licencia, dar de baja y reactivar piden motivo (5 a 300 caracteres). Solo la baja bloquea el ingreso.")
    public ResponseEntity<EmpleadoFichaDTO> cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambioEstadoEmpleadoDTO dto) {
        return ResponseEntity.ok(empleadoService.cambiarEstado(id, dto));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize(GESTION + " or @securityService.esElPropioEmpleado(#id)")
    @Operation(summary = "Historial de la ficha de un empleado")
    public ResponseEntity<List<EventoEmpleadoDTO>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(empleadoService.historial(id));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated() and !hasRole('RESTRINGIDO')")
    @Operation(summary = "El empleado modifica su email y su teléfono",
            description = "No disponible si la cuenta está suspendida, de vacaciones o de licencia.")
    public ResponseEntity<EmpleadoResponseDTO> actualizarMiFicha(
            java.security.Principal principal, @Valid @RequestBody EmpleadoPerfilUpdateDTO dto) {
        return ResponseEntity.ok(empleadoService.actualizarMiFicha(principal.getName(), dto));
    }
}