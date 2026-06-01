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
    public ResponseEntity<List<UsuarioDTO>> listarTodos(){
        return ResponseEntity.ok(usuarioService.buscarTodosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorIdPublica (@PathVariable UUID id){
        return ResponseEntity.ok(usuarioService.buscarPorIdPublica(id));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario (@RequestBody UsuarioDTO dtoUsuario, @PathVariable UUID idUsuario){
        return ResponseEntity.ok(usuarioService.actualizarUsuario(idUsuario, dtoUsuario));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> borrarUsuario (@PathVariable UUID idUsuario){
        usuarioService.borrarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }


}
