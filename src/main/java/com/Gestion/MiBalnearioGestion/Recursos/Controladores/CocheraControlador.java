package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICocheraServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/recursos-cocheras")
@RestController
public class CocheraControlador {
    private final ICocheraServicio cocheraServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CocheraDTO> crearCochera(@Valid @RequestBody CocheraDTO dto) {
        return new ResponseEntity<>(cocheraServicio.crearCochera(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CocheraDTO> actualizarCochera(@PathVariable UUID id, @Valid @RequestBody CocheraDTO dto) {
        return ResponseEntity.ok(cocheraServicio.actualizarCochera(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CocheraDTO> buscarCochera(@PathVariable UUID id) {
        return ResponseEntity.ok(cocheraServicio.buscarCochera(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CocheraDTO>> obtenerCocheras(Integer cocheraIgual,
                                                            Integer cocheraMenor,
                                                            Integer cocheraMayor) {
        return ResponseEntity.ok(cocheraServicio.listarCocheras(cocheraIgual, cocheraMenor, cocheraMayor));

    }
}
