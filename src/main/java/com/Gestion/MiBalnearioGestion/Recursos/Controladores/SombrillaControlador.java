package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.SombrillaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.SombrillaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ISombrillaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos/sombrillas")
@RequiredArgsConstructor
public class SombrillaControlador {

    private final ISombrillaServicio sombrillaServicio;

    @GetMapping
    public ResponseEntity<List<SombrillaResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) Integer numeroMayor,
            @RequestParam(required = false) Integer numeroMenor,
            @RequestParam(required = false) EtamanioSombrilla etamano) {
        return ResponseEntity.ok(sombrillaServicio.buscarTodos(numero, numeroMayor, numeroMenor, etamano));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SombrillaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(sombrillaServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SombrillaResponseDTO> crearSombrilla(@Valid @RequestBody SombrillaRequestDTO dto) {
        return new ResponseEntity<>(sombrillaServicio.crearSombrilla(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SombrillaResponseDTO> actualizarSombrilla(
            @PathVariable UUID id, @Valid @RequestBody SombrillaRequestDTO dto) {
        return ResponseEntity.ok(sombrillaServicio.actualizarSombrilla(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        sombrillaServicio.desactivarSombrilla(id);
        return ResponseEntity.noContent().build();
    }
}