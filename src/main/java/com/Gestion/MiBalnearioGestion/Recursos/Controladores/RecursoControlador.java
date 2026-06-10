package com.Gestion.MiBalnearioGestion.Recursos.Controladores;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/recursos")
@RequiredArgsConstructor
@RestController
public class RecursoControlador {
    private final IRecursoServicio recursoServicio;

    @GetMapping("/{id}")
    public ResponseEntity<RecursoDTO> BuscarRecursoPorID(@PathVariable UUID id){
        return ResponseEntity.ok(recursoServicio.buscarPorPublicId(id));
    }

    @GetMapping
    public ResponseEntity <List<RecursoDTO>> BuscarTodos(@RequestParam (required = false) String nombreIgual,
                                                         @RequestParam (required = false)String nombreContiene,
                                                         @RequestParam (required = false) Boolean reservableVerdad){
        return ResponseEntity.ok(recursoServicio.buscarTodos(nombreIgual, nombreContiene, reservableVerdad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoDTO> desactivar(@PathVariable UUID id){
        recursoServicio.desactivarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/desactivar-todos")
    public ResponseEntity<RecursoDTO> desactivarTodosRecursos(){
        recursoServicio.desactivarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/borrar-todos")
    public ResponseEntity<RecursoDTO> borrarTodos(){
        recursoServicio.borrarTodoElInventario();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecursoDTO> borrarRecurso(@PathVariable UUID id){
        recursoServicio.borrarRecurso(id);
        return ResponseEntity.noContent().build();
    }
}
