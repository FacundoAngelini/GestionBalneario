package com.Gestion.MiBalnearioGestion.Productos.Controller;

import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ECategoriaProdcuto;
import com.Gestion.MiBalnearioGestion.Productos.Service.IProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Producto Controller", description = "Endpoints para el catálogo de productos, venta, filtrado y gestión de inventario de consumos")
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping("/disponibles")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Listar productos disponibles (Público)", description = "Retorna de forma abierta el catálogo de productos que cuentan con stock activo para la venta. No requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo disponible obtenido con éxito")
    })
    public ResponseEntity<List<ProductoDTO>> listarDisponibles() {
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'MOZO', 'REPARTIDOR')")
    @Operation(summary = "Listar y filtrar todos los productos", description = "Obtiene la lista completa de productos del establecimiento aplicando filtros opcionales por coincidencia parcial de nombre, categoría o estado de disponibilidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de productos obtenido con éxito"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta con formato incorrecto", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<List<ProductoDTO>> listarTodos(
            @Parameter(description = "Filtrar por coincidencia o coincidencia parcial en el nombre", example = "Licuado") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por la categoría específica del producto") @RequestParam(required = false) ECategoriaProdcuto categoria,
            @Parameter(description = "Filtrar según si el producto se encuentra habilitado para la venta") @RequestParam(required = false) Boolean disponible) {
        return ResponseEntity.ok(productoService.listarTodos(nombre, categoria, disponible));
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO', 'MOZO', 'REPARTIDOR')")
    @Operation(summary = "Obtener un producto por ID", description = "Recupera la ficha técnica y comercial completa de un producto mediante su UUID público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ProductoDTO> obtenerProducto(
            @Parameter(description = "UUID público del producto a consultar", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
            @PathVariable UUID publicId) {
        return ResponseEntity.ok(productoService.buscar(publicId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto comercializable configurando su nombre, precio inicial y categoría. Requiere rol ADMIN o GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o JSON mal estructurado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content)
    })
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO productoNuevo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(productoNuevo));
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar un producto existente", description = "Modifica integralmente los datos comerciales de un producto mediante su UUID público. Requiere rol ADMIN o GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "ID erróneo o cuerpo de la solicitud inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ProductoDTO> actualizar(
            @Parameter(description = "UUID público del producto a modificar", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
            @PathVariable UUID publicId,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualziar(publicId, dto));
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Dar de baja un producto", description = "Realiza la baja o deshabilitación del producto del sistema mediante su UUID público. Devuelve una respuesta vacía.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto dado de baja con éxito (Sin contenido de respuesta)", content = @Content),
            @ApiResponse(responseCode = "400", description = "Formato de ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> borrar(
            @Parameter(description = "UUID público del producto a dar de baja", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
            @PathVariable UUID publicId) {
        productoService.borrar(publicId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{publicId}/reactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Reactivar un producto", description = "Vuelve a habilitar un producto previamente borrado o dado de baja para reintegrarlo al stock disponible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto reactivado con éxito"),
            @ApiResponse(responseCode = "400", description = "Formato de ID inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Rol insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> reactivar(
            @Parameter(description = "UUID público del producto a reactivar", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
            @PathVariable UUID publicId) {
        productoService.reactivar(publicId);
        return ResponseEntity.ok().build();
    }
}