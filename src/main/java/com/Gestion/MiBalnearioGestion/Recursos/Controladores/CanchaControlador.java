package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CanchaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CanchaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICanchaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos/canchas")
@RequiredArgsConstructor
public class CanchaControlador {

    private final ICanchaServicio canchaServicio;

    @GetMapping
    public ResponseEntity<List<CanchaResponseDTO>> listarTodos(
            @RequestParam(required = false) ETipoCancha tipoCancha,
            @RequestParam(required = false) Integer capacidadIgual,
            @RequestParam(required = false) Integer capacidadMayor,
            @RequestParam(required = false) Integer capacidadMenor,
            @RequestParam(required = false) Boolean iluminacion) {
        return ResponseEntity.ok(canchaServicio.buscarTodos(
                tipoCancha, capacidadIgual, capacidadMayor, capacidadMenor, iluminacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanchaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(canchaServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CanchaResponseDTO> crearCancha(@Valid @RequestBody CanchaRequestDTO dto) {
        return new ResponseEntity<>(canchaServicio.crearCancha(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CanchaResponseDTO> actualizarCancha(
            @PathVariable UUID id, @Valid @RequestBody CanchaRequestDTO dto) {
        return ResponseEntity.ok(canchaServicio.actualizarCancha(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        canchaServicio.desactivarCancha(id);
        return ResponseEntity.noContent().build();
    }
}