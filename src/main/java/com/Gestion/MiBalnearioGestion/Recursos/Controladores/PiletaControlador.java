package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
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
@RequestMapping("/recursos-pileta")
@RequiredArgsConstructor
public class PiletaControlador {
    private final IPiletaServicio  piletaServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PiletaDTO> crearPileta(@Valid @RequestBody PiletaDTO piletaDTO){
        return new ResponseEntity<>(piletaServicio.crearPileta(piletaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PiletaDTO> actualizarPileta(@Valid @RequestBody PiletaDTO piletaDTO, @PathVariable UUID Id){
        return ResponseEntity.ok(piletaServicio.actualizarPileta(piletaDTO, Id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PiletaDTO> obtenerPileta(@PathVariable UUID Id){
        return ResponseEntity.ok(piletaServicio.obtenerPileta(Id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PiletaDTO>> obtenerPiletas(@RequestParam (required = false) Boolean climatizada,
                                                          @RequestParam (required = false)Integer tamanioIgual,
                                                          @RequestParam (required = false)Integer TamanioMayor,
                                                          @RequestParam (required = false)Integer TamanioMenor){
        return ResponseEntity.ok(piletaServicio.obtenerPiletas(climatizada, tamanioIgual, TamanioMayor, TamanioMenor));
    }
}
