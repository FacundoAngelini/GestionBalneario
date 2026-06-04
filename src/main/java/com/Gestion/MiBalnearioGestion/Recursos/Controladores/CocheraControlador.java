package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICocheraServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/recrusos/cocheras")
@RestController
public class CocheraControlador {
    private final ICocheraServicio cocheraServicio;

    @PostMapping
    public ResponseEntity<CocheraDTO> crearCochera(@Valid @RequestBody CocheraDTO dto) {
        return new ResponseEntity<>(cocheraServicio.crearCochera(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<CocheraDTO> actualizarCochera(@PathVariable UUID id, @Valid @RequestBody CocheraDTO dto) {
        return ResponseEntity.ok(cocheraServicio.actualizarCochera(id, dto));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<CocheraDTO> buscarCochera(@PathVariable UUID id) {
        return ResponseEntity.ok(cocheraServicio.buscarCochera(id));
    }

    @GetMapping
    public ResponseEntity<List<CocheraDTO>> obtenerCocheras(Integer cocheraIgual,
                                                            Integer cocheraMenor,
                                                            Integer cocheraMayor) {
        return ResponseEntity.ok(cocheraServicio.listarCocheras(cocheraIgual, cocheraMenor, cocheraMayor));

    }
}
