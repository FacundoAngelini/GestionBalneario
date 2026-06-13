package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.ICarpaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RequestMapping("/recursos/carpas")
@RestController
@RequiredArgsConstructor
public class CarpaControlador {
    private final ICarpaServicio carpaServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarpaDTO> crearCarpa(@Valid @RequestBody CarpaDTO carpaDTO){
        return new ResponseEntity<>(carpaServicio.crearCarpa(carpaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarpaDTO> actualizarCarpa(@PathVariable UUID id, @Valid @RequestBody CarpaDTO carpaDTO){
        return ResponseEntity.ok(carpaServicio.actualizarCarpa(carpaDTO, id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarpaDTO> obtenerCarpaId(@PathVariable UUID id){
        return ResponseEntity.ok(carpaServicio.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarpaDTO>> obtenerCarpas(@RequestParam(required = false) Integer numero,
                                                        @RequestParam(required = false) Integer numeroMayor,
                                                        @RequestParam(required = false) Integer numeroMenor,
                                                        @RequestParam(required = false) Integer pasilloIgual,
                                                        @RequestParam(required = false) Integer pasilloMayor,
                                                        @RequestParam(required = false) Integer pasilloMenor,
                                                        @RequestParam(required = false) Integer capacidadIgual){
        return ResponseEntity.ok(carpaServicio.buscarTodos(numero, numeroMayor, numeroMenor, pasilloIgual, pasilloMayor, pasilloMenor, capacidadIgual));

    }

}
