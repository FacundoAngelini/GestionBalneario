package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPiletaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recursos-pileta")
@RequiredArgsConstructor
public class PiletaControlador {
    private final IPiletaServicio  piletaServicio;

    @PostMapping
    public ResponseEntity<PiletaDTO> crearPileta(@Valid @RequestBody PiletaDTO piletaDTO){
        return new ResponseEntity<>(piletaServicio.crearPileta(piletaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PiletaDTO> actualizarPileta(@Valid @RequestBody PiletaDTO piletaDTO, @PathVariable UUID Id){
        return ResponseEntity.ok(piletaServicio.actualizarPileta(piletaDTO, Id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiletaDTO> obtenerPileta(@PathVariable UUID Id){
        return ResponseEntity.ok(piletaServicio.obtenerPileta(Id));
    }

    @GetMapping
    public ResponseEntity<List<PiletaDTO>> obtenerPiletas(@RequestParam (required = false) boolean climatizada,
                                                          @RequestParam (required = false)boolean noClimatizada,
                                                          @RequestParam (required = false)Integer tamanioIgual,
                                                          @RequestParam (required = false)Integer TamanioMayor,
                                                          @RequestParam (required = false)Integer TamanioMenor){
        return ResponseEntity.ok(piletaServicio.obtenerPiletas(climatizada, noClimatizada, tamanioIgual, TamanioMayor, TamanioMenor));
    }
}
