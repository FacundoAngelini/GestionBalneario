package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PiletaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PiletaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPiletaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos/piletas")
@RequiredArgsConstructor
public class PiletaControlador {

    private final IPiletaServicio piletaServicio;

    @GetMapping
    public ResponseEntity<List<PiletaResponseDTO>> listarTodos(
            @RequestParam(required = false) Boolean esClimatizada,
            @RequestParam(required = false) Integer tamanio) {
        return ResponseEntity.ok(piletaServicio.buscarTodos(esClimatizada, tamanio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiletaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(piletaServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PiletaResponseDTO> crearPileta(@Valid @RequestBody PiletaRequestDTO dto) {
        return new ResponseEntity<>(piletaServicio.crearPileta(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PiletaResponseDTO> actualizarPileta(
            @PathVariable UUID id, @Valid @RequestBody PiletaRequestDTO dto) {
        return ResponseEntity.ok(piletaServicio.actualizarPileta(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        piletaServicio.desactivarPileta(id);
        return ResponseEntity.noContent().build();
    }
}