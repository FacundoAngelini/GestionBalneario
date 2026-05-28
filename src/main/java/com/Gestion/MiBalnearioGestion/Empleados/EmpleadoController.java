package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Servicio.IEmpleadoService;
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

    private final IEmpleadoService empleadoService;

    @GetMapping
    ResponseEntity<List<EmpleadoDTO>> listarTodos(@RequestParam (required = false) Integer dniIgual,
                                                  @RequestParam (required = false)  Integer dniContiene,
                                                  @RequestParam (required = false)  String nombreIgual,
                                                  @RequestParam (required = false)  String nombreContiene,
                                                  @RequestParam (required = false)  String apellidoIgual,
                                                  @RequestParam (required = false)  String apellidoContiene,
                                                  @RequestParam (required = false)  String telefonoIgual,
                                                  @RequestParam (required = false)  String telefonoContiene,
                                                  @RequestParam (required = false)  String cuitIgual,
                                                  @RequestParam (required = false)  String cuitContiene,
                                                  @RequestParam (required = false)  Double sueldoIgual,
                                                  @RequestParam (required = false)  Double sueldoMenor,
                                                  @RequestParam (required = false)  Double sueldoMayor,
                                                  @RequestParam (required = false)  String sectorIgual,
                                                  @RequestParam (required = false)  String sectorContiene,
                                                  @RequestParam (required = false)  String rolIgual,
                                                  @RequestParam (required = false)  String rolContiene,
                                                  @RequestParam (required = false)  String calleIgual,
                                                  @RequestParam (required = false)  String calleContiene,
                                                  @RequestParam (required = false)  Integer numeroIgual,
                                                  @RequestParam (required = false)  Integer numeroContiene,
                                                  @RequestParam (required = false)  String ciudadIgual,
                                                  @RequestParam (required = false)  String ciudadContiene,
                                                  @RequestParam (required = false)  String provinciaIgual,
                                                  @RequestParam (required = false)  String provinciaContiene,
                                                  @RequestParam (required = false)  EEstadoEmpleado estadoIgual,
                                                  @RequestParam (required = false)  EEstadoEmpleado estadoActivo,
                                                  @RequestParam (required = false) EEstadoEmpleado estadoInactivo){
        return ResponseEntity.ok(empleadoService.buscarTodos(dniIgual, dniContiene, nombreIgual, nombreContiene, apellidoIgual, apellidoContiene, telefonoIgual, telefonoContiene, cuitIgual, cuitContiene, sueldoIgual, sueldoMenor, sueldoMayor, sectorIgual, sectorContiene, rolIgual, rolContiene, calleIgual, calleContiene, numeroIgual, numeroContiene, ciudadIgual, ciudadContiene, provinciaIgual, provinciaContiene, estadoIgual, estadoActivo, estadoInactivo));
    }

    @GetMapping("/{id}")
    ResponseEntity<EmpleadoDTO>buscarPorId(UUID id){
        return ResponseEntity.ok(empleadoService.buscarPorIDpublico(id));
    }

    @PostMapping
    ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO EmpleadoNuevo){
        return new ResponseEntity<>(empleadoService.crearEmpleado(EmpleadoNuevo),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<EmpleadoDTO> actualizarEmpleado(@RequestBody EmpleadoDTO EmpleadoNuevo, @PathVariable UUID id){
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, EmpleadoNuevo));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminarEmpleado(@PathVariable UUID id){
        empleadoService.borrarEmpleado(id);
        return ResponseEntity.noContent().build();
    }


}
