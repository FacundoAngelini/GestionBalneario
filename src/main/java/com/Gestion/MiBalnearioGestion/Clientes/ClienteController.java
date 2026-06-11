package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ActualizarClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.CompletarPerfilClienteDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    // Admin, gerente y administración ven la lista completa
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // Admin/gerente o el propio cliente
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or " +
            "@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorPublicId(id));
    }

    // Completar perfil por primera vez tras el registro
    @PutMapping("/{id}/perfil")
    @PreAuthorize("hasRole('ADMIN') or " +
            "@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<ClienteResponseDTO> completarPerfil(
            @PathVariable UUID id,
            @Valid @RequestBody CompletarPerfilClienteDTO dto) {
        return ResponseEntity.ok(clienteService.completarPerfil(id, dto));
    }

    // Actualizar datos parcialmente
    @PatchMapping("/{id}/perfil")
    @PreAuthorize("hasRole('ADMIN') or " +
            "@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable UUID id,
            @RequestBody ActualizarClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    // Baja lógica
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<Void> darDeBaja(@PathVariable UUID id) {
        clienteService.darDeBajaCliente(id);
        return ResponseEntity.noContent().build();
    }
}