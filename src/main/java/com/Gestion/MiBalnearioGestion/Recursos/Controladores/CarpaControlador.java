package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.CarpaRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.CarpaResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICarpaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/recursos/carpas")
@RequiredArgsConstructor
public class CarpaControlador {

    private final ICarpaServicio carpaServicio;

    @GetMapping
    public ResponseEntity<List<CarpaResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) Integer numeroMayor,
            @RequestParam(required = false) Integer numeroMenor,
            @RequestParam(required = false) Integer pasilloIgual,
            @RequestParam(required = false) Integer pasilloMayor,
            @RequestParam(required = false) Integer pasilloMenor,
            @RequestParam(required = false) Integer capacidadIgual) {
        return ResponseEntity.ok(carpaServicio.buscarTodos(
                numero, numeroMayor, numeroMenor,
                pasilloIgual, pasilloMayor, pasilloMenor,
                capacidadIgual));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarpaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(carpaServicio.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CarpaResponseDTO> crearCarpa(
            @Valid @RequestBody CarpaRequestDTO dto) {
        return new ResponseEntity<>(carpaServicio.crearCarpa(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CarpaResponseDTO> actualizarCarpa(
            @PathVariable UUID id,
            @Valid @RequestBody CarpaRequestDTO dto) {
        return ResponseEntity.ok(carpaServicio.actualizarCarpa(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        carpaServicio.desactivarCarpa(id);
        return ResponseEntity.noContent().build();
    }
}