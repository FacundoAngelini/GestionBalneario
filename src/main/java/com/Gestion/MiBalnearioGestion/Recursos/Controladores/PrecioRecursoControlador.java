package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PrecioRequestRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPrecioRecursoServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/precios-recursos")
@RequiredArgsConstructor
public class PrecioRecursoControlador {

    private final IPrecioRecursoServicio precioRecursoServicio;

    @GetMapping("/{id}")
    public ResponseEntity<PrecioRecursoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(precioRecursoServicio.buscarPorPublicId(id));
    }

    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<List<PrecioRecursoResponseDTO>> buscarPorRecurso(
            @PathVariable UUID recursoId) {
        return ResponseEntity.ok(precioRecursoServicio.buscarPorRecurso(recursoId));
    }

    @GetMapping
    public ResponseEntity<List<PrecioRecursoResponseDTO>> buscarTodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vigenciaIgual,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vigenciaMenor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vigenciaMayor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate caducadaIgual,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate caducadaMenor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate caducadaMayor,
            @RequestParam(required = false) Double precioIgual,
            @RequestParam(required = false) Double precioMenor,
            @RequestParam(required = false) Double precioMayor) {
        return ResponseEntity.ok(precioRecursoServicio.buscarTodos(
                vigenciaIgual, vigenciaMenor, vigenciaMayor,
                caducadaIgual, caducadaMenor, caducadaMayor,
                precioIgual, null, precioMayor));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PrecioRecursoResponseDTO> crearPrecio(
            @Valid @RequestBody PrecioRequestRecursoDTO dto) {
        return new ResponseEntity<>(precioRecursoServicio.crearPrecio(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> eliminarPrecio(@PathVariable UUID id) {
        precioRecursoServicio.eliminarPrecio(id);
        return ResponseEntity.noContent().build();
    }
}