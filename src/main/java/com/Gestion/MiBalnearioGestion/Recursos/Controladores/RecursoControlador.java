package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.RecursoRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.RecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos")
@RequiredArgsConstructor
public class RecursoControlador {

    private final IRecursoServicio recursoServicio;

    // GET /api/v1/recursos
    @GetMapping
    public ResponseEntity<List<RecursoResponseDTO>> listarTodos(
            @RequestParam(required = false) String nombreIgual,
            @RequestParam(required = false) String nombreContiene,
            @RequestParam(required = false) Boolean esReservable) {
        return ResponseEntity.ok(
                recursoServicio.buscarTodos(nombreIgual, nombreContiene, esReservable));
    }

    // GET /api/v1/recursos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RecursoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(recursoServicio.buscarPorPublicId(id));
    }

    // GET /api/v1/recursos/sector/{sectorId}
    @GetMapping("/sector/{sectorId}")
    public ResponseEntity<List<RecursoResponseDTO>> buscarPorSector(@PathVariable UUID sectorId) {
        return ResponseEntity.ok(recursoServicio.buscarPorSector(sectorId));
    }

    // PATCH /api/v1/recursos/{id}/activar
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {
        recursoServicio.activarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/v1/recursos/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        recursoServicio.desactivarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/v1/recursos/inventario/desactivar-todo
    @PatchMapping("/inventario/desactivar-todo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarTodo() {
        recursoServicio.desactivarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/recursos/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarRecurso(@PathVariable UUID id) {
        recursoServicio.borrarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/recursos/inventario
    // Solo funciona si todos los recursos están desactivados (la validación está en el servicio)
    @DeleteMapping("/inventario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarInventario() {
        recursoServicio.borrarTodoElInventario();
        return ResponseEntity.noContent().build();
    }
}
