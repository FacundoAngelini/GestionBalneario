package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CocheraRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CocheraResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICocheraServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos/cocheras")
@RequiredArgsConstructor
public class CocheraControlador {

    private final ICocheraServicio cocheraServicio;

    @GetMapping
    public ResponseEntity<List<CocheraResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer numeroCochera,
            @RequestParam(required = false) Integer numeroMayor,
            @RequestParam(required = false) Integer numeroMenor) {
        return ResponseEntity.ok(cocheraServicio.buscarTodos(numeroCochera, numeroMayor, numeroMenor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CocheraResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cocheraServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CocheraResponseDTO> crearCochera(@Valid @RequestBody CocheraRequestDTO dto) {
        return new ResponseEntity<>(cocheraServicio.crearCochera(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CocheraResponseDTO> actualizarCochera(
            @PathVariable UUID id, @Valid @RequestBody CocheraRequestDTO dto) {
        return ResponseEntity.ok(cocheraServicio.actualizarCochera(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        cocheraServicio.desactivarCochera(id);
        return ResponseEntity.noContent().build();
    }
}