package com.Gestion.MiBalnearioGestion.Productos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>>listarTodos()
    {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO>obtenerProducto(@PathVariable UUID publicId)
    {
        return ResponseEntity.ok(productoService.buscar(publicId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>borrar(@PathVariable UUID publicId)
    {
        productoService.borrar(publicId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO>crear(@Valid @RequestBody ProductoDTO productoNuevo)
    {
        return ResponseEntity.ok(productoService.crear(productoNuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO>actualizar(@PathVariable UUID publicId, @Valid @RequestBody ProductoDTO dto)
    {
        return ResponseEntity.ok(productoService.actualziar(publicId,dto));
    }


}
