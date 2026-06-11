package com.Gestion.MiBalnearioGestion.Usuarios.Controlador;

import com.Gestion.MiBalnearioGestion.Usuarios.DTO.ActualizarPerfilRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarNombreUsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Servicio.IUsuarioService;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String usernameContiene,
            @RequestParam(required = false) Boolean activos,
            @RequestParam(required = false) Boolean noActivo) {
        return ResponseEntity.ok(
                usuarioService.buscarTodosUsuarios(username, usernameContiene, activos, noActivo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRACION') or @securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<UsuarioDTO> buscarPorIdPublica(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorIdPublica(id));
    }

    @PutMapping("/{id}/perfil")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarPerfilRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    @PatchMapping("/{id}/contrasenia")
    @PreAuthorize("@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<Void> cambiarContrasenia(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarContraseniaRequest request) {
        usuarioService.cambiarContrasenia(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<Void> darDeBaja(@PathVariable UUID id) {
        usuarioService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/nombre-usuario")
    @PreAuthorize("@securityService.esElMismoUsuario(authentication, #id)")
    public ResponseEntity<Void> cambiarNombreUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarNombreUsuarioDTO request) {
        usuarioService.cambiarNombreUsuario(id, request);
        return ResponseEntity.noContent().build();
    }
}
