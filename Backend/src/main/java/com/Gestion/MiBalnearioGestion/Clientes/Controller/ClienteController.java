package com.Gestion.MiBalnearioGestion.Clientes.Controller;

import com.Gestion.MiBalnearioGestion.Clientes.Service.ClienteGestionService;
import com.Gestion.MiBalnearioGestion.Clientes.Service.IClienteService;
import com.Gestion.MiBalnearioGestion.Clientes.dto.*;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioNombreUsuarioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Cliente Controller", description = "Endpoints para la gestión de legajos de clientes, auditoría de perfiles, control de cuentas y actualización de credenciales de usuario")
public class ClienteController {

    private final IClienteService clienteService;
    private final ClienteGestionService gestionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listado dinámico y filtrado de clientes", description = "Motor de búsqueda avanzado para la administración. Permite consultar el padrón de clientes registrados combinando filtros exactos o parciales por datos de contacto e identidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de clientes recuperado con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios de gestión jerárquica requeridos", content = @Content)
    })
    public ResponseEntity<List<ClienteResponse>> listarTodos(
            @Parameter(description = "Nombre exacto a buscar") @RequestParam(required = false) String nombreIgual,
            @Parameter(description = "Fragmento del nombre") @RequestParam(required = false) String nombreContiene,
            @Parameter(description = "Apellido exacto a buscar") @RequestParam(required = false) String apellidoIgual,
            @Parameter(description = "Fragmento del apellido") @RequestParam(required = false) String apellidoContiene,
            @Parameter(description = "DNI exacto del cliente", example = "38123456") @RequestParam(required = false) Integer dniIgual,
            @Parameter(description = "Fragmento de la dirección de correo electrónico") @RequestParam(required = false) String emailContiene,
            @Parameter(description = "Número telefónico exacto") @RequestParam(required = false) String telefonoIgual,
            @Parameter(description = "Filtrar por estado de la cuenta (true: Activo, false: Inactivo)") @RequestParam(required = false) Boolean estadoIgual) {
        return ResponseEntity.ok(clienteService.listarTodos(nombreIgual, nombreContiene, apellidoIgual, apellidoContiene, dniIgual, emailContiene, telefonoIgual, estadoIgual));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    @Operation(summary = "Buscar un cliente por DNI", description = "Devuelve solo nombre, apellido y DNI. Lo usa el personal en el mostrador.")
    public ResponseEntity<ClienteBusquedaDTO> buscarPorDni(@RequestParam Integer dni) {
        return ResponseEntity.ok(clienteService.buscarPorDni(dni));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION') or @securityService.esElPropioCliente(#id)")
    @Operation(summary = "Buscar cliente por ID público", description = "Recupera el perfil completo de un cliente mediante su UUID. Permitido para el staff administrativo o para el propio cliente titular de la cuenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del cliente recuperado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Restricción por propiedad de recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "El cliente solicitado no existe", content = @Content)
    })
    public ResponseEntity<ClienteResponse> buscarPorIdPublico(
            @Parameter(description = "UUID público del cliente a consultar", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorIDpublico(id));
    }


    @PatchMapping("/{id}/nombre-usuario")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElPropioCliente(#id)")
    @Operation(summary = "Modificar nombre de usuario del cliente", description = "Permite cambiar el identificador único de acceso (`username`) del sistema de login del cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nombre de usuario actualizado con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - El nuevo nombre de usuario ya está en uso", content = @Content)
    })
    public ResponseEntity<Void> cambiarNombreUsuario(
            @Parameter(description = "UUID público del cliente", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d") @PathVariable UUID id,
            @Valid @RequestBody CambioNombreUsuarioRequest request) {
        clienteService.cambiarNombreUsuarioCliente(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/baja")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "El cliente se da de baja", description = "Pide motivo. Se rechaza si tiene reservas pendientes o confirmadas.")
    public ResponseEntity<Void> darmeDeBaja(
            @Parameter(hidden = true) Principal principal, @Valid @RequestBody BajaPropiaDTO body) {
        gestionService.darseDeBaja(principal.getName(), body.motivo());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/contrasenia")
    @PreAuthorize("@securityService.esElPropioCliente(#id)")
    @Operation(summary = "Actualizar contraseña del cliente", description = "Operación crítica y privada. Permite al cliente autenticado renovar su clave de seguridad personal. Por motivos de privacidad, el personal administrativo no tiene acceso a este endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contraseña modificada con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - No es el titular legítimo de la cuenta", content = @Content)
    })
    public ResponseEntity<Void> cambiarContrasenia(
            @Parameter(description = "UUID público del cliente titular", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d") @PathVariable UUID id,
            @Valid @RequestBody CambioContraseniaRequest request) {
        clienteService.cambiarContraseniaCliente(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Actualizar el perfil del cliente autenticado",
            description = "Permite modificar nombre, apellido, email y teléfono. No permite modificar el DNI ni las credenciales."
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email o teléfono ya registrado", content = @Content)
    })
    public ResponseEntity<ClienteResponse> actualizarMiPerfil(@Parameter(hidden = true) Principal principal, @Valid @RequestBody ClienteUpdateRequest request)
    {
        return ResponseEntity.ok(clienteService.actualizarMiPerfil(principal.getName(), request));
    }
}