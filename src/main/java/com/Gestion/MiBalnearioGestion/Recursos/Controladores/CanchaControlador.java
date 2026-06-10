package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICanchaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/recursos/canchas")
@RestController
public class CanchaControlador {
    private final ICanchaServicio canchaServicio;

    @PostMapping
    public ResponseEntity<CanchaDTO> crearCancha(@Valid @RequestBody CanchaDTO cancha) {
        return new ResponseEntity<>(canchaServicio.crearCancha(cancha),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanchaDTO> actualizarCancha(@PathVariable UUID id, @Valid @RequestBody CanchaDTO cancha) {
        return ResponseEntity.ok(canchaServicio.actualizarCancha(id, cancha));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<CanchaDTO> obtenerCanchaId(@PathVariable UUID id) {
        return ResponseEntity.ok(canchaServicio.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CanchaDTO>> obtenerCanchas(ETipoCancha cancha,
                                                          Integer capacidadIgual,
                                                          Integer capacidadMenor,
                                                          Integer capacidadMayor,
                                                          boolean iluminacion,
                                                          boolean noIluminacion) {
        return ResponseEntity.ok(canchaServicio.buscarTodas(cancha, capacidadIgual, capacidadMenor, capacidadMayor, iluminacion, noIluminacion));
    }
}
