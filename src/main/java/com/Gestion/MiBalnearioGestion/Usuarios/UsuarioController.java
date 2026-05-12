package com.Gestion.MiBalnearioGestion.Usuarios;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/usuarios")

@AllArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    ResponseEntity<List<UsuarioDTO>> listarTodos(){
        return ResponseEntity.ok(usuarioService.buscarTodosUsuarios());
    }

    @GetMapping("{/id}")
    ResponseEntity<UsuarioDTO> buscarPorIdPublica (@PathVariable UUID id){
        return ResponseEntity.ok(usuarioService.buscarPorIdPublica(id));
    }

    @GetMapping("{/nombreUsuario}")
    ResponseEntity<UsuarioDTO> buscarPorNombreUsuario (@PathVariable String nombreUsuario){
        return ResponseEntity.ok(usuarioService.buscarPorNombreUsuario(nombreUsuario));
    }

    @PostMapping
    ResponseEntity<UsuarioDTO> crearUsuario (@RequestBody UsuarioDTO dtoUsuario){
        return new ResponseEntity<>(usuarioService.crearUsuario(dtoUsuario), HttpStatus.CREATED);
    }

    @PutMapping("{/usuarioID}")
    ResponseEntity<UsuarioDTO> actualizarUsuario (@RequestBody UsuarioDTO dtoUsuario, @PathVariable UUID idUsuario){
        return ResponseEntity.ok(usuarioService.actualizarUsuario(idUsuario,dtoUsuario));
    }

    @DeleteMapping("{/usuarioID}")
    ResponseEntity<Void> borrarUsuario (@PathVariable UUID idUsuario){
        usuarioService.borrarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }



}
