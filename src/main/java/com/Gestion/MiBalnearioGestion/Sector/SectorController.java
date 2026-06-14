package com.Gestion.MiBalnearioGestion.Sector;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sectores")
@RequiredArgsConstructor
public class SectorController {

    private final ISectorService sectorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SectorDTO>> listarTodos() {
        return ResponseEntity.ok(sectorService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectorDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(sectorService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectorDTO> crearSector(
            @Valid @RequestBody SectorDTO dto) {
        return new ResponseEntity<>(sectorService.crearSector(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectorDTO> actualizarSector(
            @PathVariable UUID id,
            @Valid @RequestBody SectorDTO dto) {
        return ResponseEntity.ok(sectorService.actualizarSector(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarSector(@PathVariable UUID id) {
        sectorService.borrarSector(id);
        return ResponseEntity.noContent().build();
    }
}