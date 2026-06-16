package com.Gestion.MiBalnearioGestion.Productos.Controller;

import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ECategoriaProdcuto;
import com.Gestion.MiBalnearioGestion.Productos.Service.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping("/disponibles")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductoDTO>> listarDisponibles() {
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'MOZO', 'REPARTIDOR')")
    public ResponseEntity<List<ProductoDTO>> listarTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) ECategoriaProdcuto categoria,
            @RequestParam(required = false) Boolean disponible) {

        return ResponseEntity.ok(productoService.listarTodos(nombre, categoria, disponible));
    }


    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'MOZO', 'REPARTIDOR')")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable UUID publicId) {
        return ResponseEntity.ok(productoService.buscar(publicId));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO productoNuevo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(productoNuevo));
    }


    @PutMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable UUID publicId,
                                                  @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualziar(publicId, dto));
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Void> borrar(@PathVariable UUID publicId) {
        productoService.borrar(publicId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{publicId}/reactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Void> reactivar(@PathVariable UUID publicId) {
        productoService.reactivar(publicId);
        return ResponseEntity.ok().build();
    }
}