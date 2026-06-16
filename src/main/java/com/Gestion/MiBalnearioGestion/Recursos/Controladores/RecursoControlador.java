package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/recursos")
@RequiredArgsConstructor
@RestController
public class RecursoControlador {
    private final IRecursoServicio recursoServicio;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity<RecursoDTO> BuscarRecursoPorID(@PathVariable UUID id){
        return ResponseEntity.ok(recursoServicio.buscarPorPublicId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ADMINISTRATIVO')")
    public ResponseEntity <List<RecursoDTO>> BuscarTodos(@RequestParam (required = false) String nombreIgual,
                                                         @RequestParam (required = false)String nombreContiene,
                                                         @RequestParam (required = false) Boolean reservableVerdad){
        return ResponseEntity.ok(recursoServicio.buscarTodos(nombreIgual, nombreContiene, reservableVerdad));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoDTO> desactivar(@PathVariable UUID id){
        recursoServicio.desactivarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/desactivar-todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoDTO> desactivarTodosRecursos(){
        recursoServicio.desactivarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/borrar-todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoDTO> borrarTodos(){
        recursoServicio.borrarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoDTO> borrarRecurso(@PathVariable UUID id){
        recursoServicio.borrarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<RecursoDTO>> obtenerRecursosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<RecursoDTO> disponibles = recursoServicio.listarDisponiblesParaElCliente(fechaInicio, fechaFin);
        return ResponseEntity.ok(disponibles);
    }
}
