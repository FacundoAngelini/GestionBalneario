package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IMesaServcio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recursos-mesas")
@RequiredArgsConstructor
public class MesaControlador {
    private final IMesaServcio mesaServcio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> crearMesa (@Valid @RequestBody MesaDTO mesaDTO){
        return new ResponseEntity<>(mesaServcio.crearMesa(mesaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> actualizarMesa (@Valid @RequestBody MesaDTO mesaDTO, @PathVariable UUID id){
        return ResponseEntity.ok(mesaServcio.actualizarMesa(mesaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> obtenerMesaId(@PathVariable UUID id){
        return ResponseEntity.ok(mesaServcio.obtenerMesaId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MesaDTO>> obtenerMesas(@RequestParam (required = false) Integer numeroIgual,
                                                      @RequestParam (required = false) Integer numeroMenor,
                                                      @RequestParam (required = false) Integer numeroMayor,
                                                      @RequestParam (required = false) Integer capacidadIgual,
                                                      @RequestParam (required = false) Integer capacidadMenor,
                                                      @RequestParam (required = false) Integer capacidadMayor){
        return ResponseEntity.ok(mesaServcio.obtenerMesas(numeroIgual, numeroMenor, numeroMayor, capacidadIgual, capacidadMenor, capacidadMayor));

    }

}
