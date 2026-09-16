package com.Gestion.MiBalnearioGestion.Clientes.Controller;

import com.Gestion.MiBalnearioGestion.Clientes.Service.IClienteService;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
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

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Cliente Controller", description = "Endpoints para la gestión de legajos de clientes, auditoría de perfiles, control de cuentas y actualización de credenciales de usuario")
public class ClienteController {

    private final IClienteService clienteService;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    @Operation(summary = "Registrar un nuevo cliente de forma administrativa", description = "Permite al personal del establecimiento dar de alta un cliente de forma manual, vinculando sus datos filiatorios y credenciales iniciales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "211", description = "Cliente creado con éxito"), // Equivale a 201 Created
            @ApiResponse(responseCode = "400", description = "Payload de entrada inválido o falla en las restricciones de validación", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol administrativo", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - El DNI, Email o Nombre de Usuario ya se encuentra registrado", content = @Content)
    })
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest clienteNuevo) {
        return new ResponseEntity<>(clienteService.crearCliente(clienteNuevo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or @securityService.esElPropioCliente(#id)")
    @Operation(summary = "Actualizar perfil de cliente", description = "Modifica los datos personales y de contacto de un cliente existente. Accesible por la administración o por el propio usuario dueño del perfil.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de cliente modificado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos o ID corrupto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró el cliente especificado", content = @Content)
    })
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @Parameter(description = "UUID público del cliente a modificar", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d") @PathVariable UUID id,
            @Valid @RequestBody ClienteRequest clienteNuevo) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteNuevo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElPropioCliente(#id)")
    @Operation(summary = "Dar de baja lógicamente una cuenta de cliente", description = "Deshabilita el estado de la cuenta (`estado = false`), bloqueando el inicio de sesión del cliente pero preservando sus datos históricos de consumos y reservas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta de cliente dada de baja con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    public ResponseEntity<Void> borrarCliente(
            @Parameter(description = "UUID público del cliente a dar de baja", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id) {
        clienteService.borrarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar cuenta de cliente", description = "Restablece el acceso y operatividad de una cuenta de cliente previamente suspendida o dada de baja lógicamente (`estado = true`).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta de cliente reactivada con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Operación exclusiva del Administrador", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    public ResponseEntity<ClienteResponse> reactivarCliente(
            @Parameter(description = "UUID público del cliente a reactivar", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.reactivarCliente(id));
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
}