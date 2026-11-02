package com.Gestion.MiBalnearioGestion.Empleados.Controller;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoUpdateDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Servicio.IEmpleadoService;
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
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(name = "Empleado Controller", description = "Endpoints de recursos humanos para la gestión de legajos, asignación de roles/sectores, auditoría de personal y control de credenciales")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listado dinámico y filtrado de empleados", description = "Motor de búsqueda avanzado para la gerencia de RRHH. Permite realizar consultas combinando filtros de coincidencia exacta (Igual) o parcial (Contiene) sobre datos personales, financieros, sectoriales y de localización domiciliaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de empleados recuperado con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requieren privilegios de administración o gerencia", content = @Content)
    })
    public ResponseEntity<List<EmpleadoResponseDTO>> listarTodos(
            @Parameter(description = "DNI exacto a buscar") @RequestParam(required = false) Integer dniIgual,
            @Parameter(description = "Fragmento numérico del DNI") @RequestParam(required = false) Integer dniContiene,
            @Parameter(description = "Nombre exacto del empleado") @RequestParam(required = false) String nombreIgual,
            @Parameter(description = "Fragmento del nombre") @RequestParam(required = false) String nombreContiene,
            @Parameter(description = "Apellido exacto del empleado") @RequestParam(required = false) String apellidoIgual,
            @Parameter(description = "Fragmento del apellido") @RequestParam(required = false) String apellidoContiene,
            @Parameter(description = "Teléfono exacto a buscar") @RequestParam(required = false) String telefonoIgual,
            @Parameter(description = "Fragmento del número telefónico") @RequestParam(required = false) String telefonoContiene,
            @Parameter(description = "CUIT exacto a buscar") @RequestParam(required = false) String cuitIgual,
            @Parameter(description = "Fragmento del CUIT") @RequestParam(required = false) String cuitContiene,
            @Parameter(description = "Sueldo base exacto a buscar", example = "450000.00") @RequestParam(required = false) Double sueldoIgual,
            @Parameter(description = "Tope máximo de sueldo para el filtro (Menor o igual a)", example = "300000.00") @RequestParam(required = false) Double sueldoMenor,
            @Parameter(description = "Piso mínimo de sueldo para el filtro (Mayor o igual a)", example = "500000.00") @RequestParam(required = false) Double sueldoMayor,
            @Parameter(description = "Nombre exacto del sector operativo") @RequestParam(required = false) String sectorIgual,
            @Parameter(description = "Fragmento del nombre del sector") @RequestParam(required = false) String sectorContiene,
            @Parameter(description = "Nombre del rol de seguridad exacto") @RequestParam(required = false) String rolIgual,
            @Parameter(description = "Fragmento del rol de seguridad") @RequestParam(required = false) String rolContiene,
            @Parameter(description = "Nombre exacto de la calle de residencia") @RequestParam(required = false) String calleIgual,
            @Parameter(description = "Fragmento del nombre de la calle") @RequestParam(required = false) String calleContiene,
            @Parameter(description = "Altura domiciliaria exacta") @RequestParam(required = false) Integer numeroIgual,
            @Parameter(description = "Fragmento numérico de la altura") @RequestParam(required = false) Integer numeroContiene,
            @Parameter(description = "Ciudad o localidad exacta") @RequestParam(required = false) String ciudadIgual,
            @Parameter(description = "Fragmento del nombre de la ciudad") @RequestParam(required = false) String ciudadContiene,
            @Parameter(description = "Provincia o región administrativa exacta") @RequestParam(required = false) String provinciaIgual,
            @Parameter(description = "Fragmento del nombre de la provincia") @RequestParam(required = false) String provinciaContiene,
            @Parameter(description = "Estado de disponibilidad del empleado en el sistema, default= ACTIVO") @RequestParam(required = false, defaultValue = "ACTIVO") EEstadoEmpleado estadoIgual) {
        return ResponseEntity.ok(empleadoService.listarEmpleados(dniIgual, dniContiene, nombreIgual, nombreContiene, apellidoIgual, apellidoContiene, telefonoIgual, telefonoContiene, cuitIgual, cuitContiene, sueldoIgual, sueldoMenor, sueldoMayor, sectorIgual, sectorContiene, rolIgual, rolContiene, calleIgual, calleContiene, numeroIgual, numeroContiene, ciudadIgual, ciudadContiene, provinciaIgual, provinciaContiene, estadoIgual));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE') or @securityService.esElPropioEmpleado(#id)")
    @Operation(summary = "Buscar empleado por ID público", description = "Recupera la ficha completa de un empleado a través de su UUID público. Accesible para la jerarquía o para el propio empleado titular del legajo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha del empleado recuperada con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Restricción por propiedad de cuenta o rol jerárquico", content = @Content),
            @ApiResponse(responseCode = "404", description = "El empleado solicitado no existe", content = @Content)
    })
    public ResponseEntity<EmpleadoResponseDTO> buscarPorId(
            @Parameter(description = "UUID público del empleado a consultar", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @PathVariable UUID id){
        return ResponseEntity.ok(empleadoService.buscarPorIDpublico(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Registrar un nuevo empleado con credenciales", description = "Da de alta un nuevo legajo en el sistema (junto con su dirección física) y genera de forma simultánea su cuenta de usuario y contraseña de acceso inicial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "211", description = "Empleado registrado con éxito junto a sus componentes"), // Spring mapea 201 Created
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o fallas en las restricciones de validación del payload", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol jerárquico requerido", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un empleado con el mismo DNI, CUIT, Email o Nombre de Usuario", content = @Content)
    })
    public ResponseEntity<EmpleadoResponseDTO> crearEmpleado(@Valid @RequestBody EmpleadoDTO EmpleadoNuevo){
        return new ResponseEntity<>(empleadoService.crearEmpleado(EmpleadoNuevo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Actualizar ficha de empleado por ID", description = "Modifica las condiciones contractuales, de sueldo, datos personales, domicilio o asignación de rol/sector de un empleado existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha de empleado actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de modificación inconsistentes o ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró el empleado a modificar", content = @Content)
    })
    public ResponseEntity<EmpleadoResponseDTO> actualizar(
            @Parameter(description = "UUID público del empleado a actualizar", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @PathVariable UUID id,
            @Valid @RequestBody EmpleadoUpdateDTO dto){
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dar de baja lógicamente un empleado", description = "Pone al empleado en estado 'INACTIVO' (o estado equivalente de baja) quitándole los permisos de inicio de sesión pero preservando su historial transaccional por motivos de auditoría de caja.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado dado de baja con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Operación destructiva exclusiva del rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminarEmpleado(
            @Parameter(description = "UUID público del empleado a dar de baja", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @PathVariable UUID id){
        empleadoService.borrarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Reactivar un empleado inactivo", description = "Revierte la baja lógica de un empleado regresándolo a estado 'ACTIVO' y devolviéndole la capacidad de interactuar con el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado reactivado con éxito"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<EmpleadoResponseDTO> reactivarEmpleado(
            @Parameter(description = "UUID público del empleado a reactivar", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
            @PathVariable UUID id) {
        return ResponseEntity.ok(empleadoService.reactivarEmpleado(id));
    }

    @PatchMapping("/{id}/nombre-usuario")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElPropioEmpleado(#id)")
    @Operation(summary = "Modificar identificador/nombre de usuario", description = "Cambia el nombre único de acceso (`username`) asignado al sistema de login del empleado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nombre de usuario modificado con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - No es el dueño de la cuenta ni un administrador", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - El nuevo nombre de usuario ya está tomado en el sistema", content = @Content)
    })
    public ResponseEntity<Void> cambiarNombreUsuario(
            @Parameter(description = "UUID público del empleado", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d") @PathVariable UUID id,
            @Valid @RequestBody CambioNombreUsuarioRequest request) {
        empleadoService.cambiarNombreUsuarioEmpleado(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/contrasenia")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElPropioEmpleado(#id)")
    @Operation(summary = "Actualizar contraseña de seguridad", description = "Permite la renovación de la clave secreta del empleado. Valida la estructura del payload antes del proceso de cifrado en la capa de servicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contraseña actualizada con éxito (Sin contenido)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    public ResponseEntity<Void> cambiarContrasenia(
            @Parameter(description = "UUID público del empleado", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d") @PathVariable UUID id,
            @Valid @RequestBody CambioContraseniaRequest request) {
        empleadoService.cambiarContraseniaEmpleado(id, request);
        return ResponseEntity.noContent().build();
    }
}