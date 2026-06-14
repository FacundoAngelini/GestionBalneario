package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
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
@RequestMapping("/api/v1/recursos-sombrillas")
@RequiredArgsConstructor
public class SombrillaControlador {
    private final ISombrillaServicio sombrillaServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SombrillaDTO> crearSombrilla (@Valid @RequestBody SombrillaDTO sombrillaDTO) {
        return new ResponseEntity<>(sombrillaServicio.crearSombrilla(sombrillaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SombrillaDTO> actualizarSombrilla (@Valid @RequestBody SombrillaDTO sombrillaDTO, @PathVariable UUID id) {
        return ResponseEntity.ok(sombrillaServicio.actualizarSombrilla(sombrillaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SombrillaDTO> obtenerSombrilla (@PathVariable UUID id) {
        return ResponseEntity.ok(sombrillaServicio.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SombrillaDTO>> obtenerSombrillas (@RequestParam(required = false) Integer numero,
                                                                 @RequestParam(required = false) Integer numeroMenor,
                                                                 @RequestParam(required = false) Integer numeroMayor,
                                                                 @RequestParam(required = false) EtamanioSombrilla tamanio) {
        return ResponseEntity.ok(sombrillaServicio.buscarTodas(numero, numeroMenor, numeroMayor, tamanio));
    }
}
