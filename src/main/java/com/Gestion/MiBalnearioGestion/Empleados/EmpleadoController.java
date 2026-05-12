package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/empleados")

@AllArgsConstructor
public class EmpleadoController {

    private final IEmpleadoServicio empleadoService;

    @GetMapping
    ResponseEntity<List<EmpleadoDTO>> listarTodos(){
        return ResponseEntity.ok(empleadoService.buscarTodos());
    }

    @GetMapping("{/id}")
    ResponseEntity<EmpleadoDTO>buscarPorId(UUID id){
        return ResponseEntity.ok(empleadoService.buscarPorIDpublico(id));
    }

    @PostMapping
    ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO EmpleadoNuevo){
        return new ResponseEntity<EmpleadoDTO>(empleadoService.crearEmpleado(EmpleadoNuevo),HttpStatus.CREATED);
    }

    @PutMapping("{/empleadoID}")
    ResponseEntity<EmpleadoDTO> actualizarEmpleado(@RequestBody EmpleadoDTO EmpleadoNuevo, @PathVariable UUID id){
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, EmpleadoNuevo));
    }

    @DeleteMapping("{empeladoID}")
    ResponseEntity<Void> eliminarEmpleado(@PathVariable UUID id){
        empleadoService.borrarEmpleado(id);
        return ResponseEntity.noContent().build();
    }


}
