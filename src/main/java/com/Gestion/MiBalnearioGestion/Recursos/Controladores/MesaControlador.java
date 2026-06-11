package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.MesaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.MesaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IMesaServcio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos/mesas")
@RequiredArgsConstructor
public class MesaControlador {

    private final IMesaServcio mesaServicio;

    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) Integer numeroMayor,
            @RequestParam(required = false) Integer numeroMenor,
            @RequestParam(required = false) Integer capacidadIgual,
            @RequestParam(required = false) Integer capacidadMayor,
            @RequestParam(required = false) Integer capacidadMenor) {
        return ResponseEntity.ok(mesaServicio.buscarTodos(
                numero, numeroMayor, numeroMenor,
                capacidadIgual, capacidadMayor, capacidadMenor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(mesaServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MesaResponseDTO> crearMesa(@Valid @RequestBody MesaRequestDTO dto) {
        return new ResponseEntity<>(mesaServicio.crearMesa(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MesaResponseDTO> actualizarMesa(
            @PathVariable UUID id, @Valid @RequestBody MesaRequestDTO dto) {
        return ResponseEntity.ok(mesaServicio.actualizarMesa(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        mesaServicio.desactivarMesa(id);
        return ResponseEntity.noContent().build();
    }
}