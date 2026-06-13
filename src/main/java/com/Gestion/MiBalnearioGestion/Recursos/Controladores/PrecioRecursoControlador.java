package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PrecioRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
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
@RequestMapping("/precios-recursos")
@RequiredArgsConstructor
public class PrecioRecursoControlador {
    private final IPrecioRecursoServicio precioRecursoServicio;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrecioRecursoDTO> crearPrecio(@Valid @RequestBody PrecioRecursoDTO precioRecursoDTO){
        return new ResponseEntity<>(precioRecursoServicio.crearPrecio(precioRecursoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrecioRecursoDTO> obtenerPrecio(@PathVariable UUID id){
        return ResponseEntity.ok(precioRecursoServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrecioRecursoDTO>> obtenerPrecio(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate precioVigenciaIgual,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioVigenciaMenor,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioVigenciaMayor,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoIgual,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoMenor,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate precioCaducidoMayor,
                                                                @RequestParam(required = false) Double precioIgual,
                                                                @RequestParam(required = false) Double precioMenor,
                                                                @RequestParam(required = false) Double precioMayor){
        return ResponseEntity.ok(precioRecursoServicio.buscarTodos(precioVigenciaIgual, precioVigenciaMenor, precioVigenciaMayor, precioCaducidoIgual, precioCaducidoMenor, precioCaducidoMayor, precioIgual, precioMenor, precioMayor));

    }
}
