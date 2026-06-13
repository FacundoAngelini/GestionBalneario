package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Usuarios.CambioContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.CambioNombreUsuarioRequest;
import jakarta.validation.Valid;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ClienteResponse>> listarTodos(
            @RequestParam(required = false) String nombreIgual,
            @RequestParam(required = false) String nombreContiene,
            @RequestParam(required = false) String apellidoIgual,
            @RequestParam(required = false) String apellidoContiene,
            @RequestParam(required = false) Integer dniIgual,
            @RequestParam(required = false) String emailContiene,
            @RequestParam(required = false) String telefonoIgual,
            @RequestParam(required = false) Boolean estadoIgual) {
        return ResponseEntity.ok(clienteService.listarTodos(nombreIgual, nombreContiene, apellidoIgual, apellidoContiene, dniIgual, emailContiene, telefonoIgual, estadoIgual));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION') or @securityService.esElPropioCliente(#id)")
    public ResponseEntity<ClienteResponse> buscarPorIdPublico(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorIDpublico(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ClienteResponse> crearCliente(
            @Valid @RequestBody ClienteRequest clienteNuevo) {
        return new ResponseEntity<>(clienteService.crearCliente(clienteNuevo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or @securityService.esElPropioCliente(#id)")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteRequest clienteNuevo) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteNuevo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarCliente(@PathVariable UUID id) {
        clienteService.borrarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ClienteResponse> reactivarCliente(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.reactivarCliente(id));
    }

    @PatchMapping("/{id}/nombre-usuario")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElPropioCliente(#id)")
    ResponseEntity<Void> cambiarNombreUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody CambioNombreUsuarioRequest request) {
        clienteService.cambiarNombreUsuarioCliente(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/contrasenia")
    @PreAuthorize("@securityService.esElPropioCliente(#id)")
    ResponseEntity<Void> cambiarContrasenia(
            @PathVariable UUID id,
            @Valid @RequestBody CambioContraseniaRequest request) {
        clienteService.cambiarContraseniaCliente(id, request);
        return ResponseEntity.noContent().build();
    }
}