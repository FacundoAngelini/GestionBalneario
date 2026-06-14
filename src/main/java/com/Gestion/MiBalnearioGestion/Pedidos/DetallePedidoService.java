package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.ProductoException;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.DetallePedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoMapper detallePedidoMapper;
    private final iDetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public DetallePedidoEntity crearDetallePedido(DetallePedidoRequest request, PedidoEntity pedido) {

        ProductoEntity producto = productoRepository.findByPublicId(request.getProductoId())
                .orElseThrow(() -> new ProductoException("Producto no encontrado"));

        if (!producto.isProductoDisponible()) {
            throw new ProductoException("Producto no disponible: " + producto.getNombre());
        }

        DetallePedidoEntity detalle = DetallePedidoEntity.builder()
                .cantidad(request.getCantidad())
                .precio(request.getPrecio())
                .producto(producto)
                .pedido(pedido)
                .build();

        return detallePedidoRepository.save(detalle);
    }


    public List<DetallePedidoResponse> obtenerDetallesPorPedido(UUID pedidoPublicId) {
        return detallePedidoRepository.findByPedidoPublicId(pedidoPublicId).stream()
                .map(detallePedidoMapper::convertToResponseDTO)
                .collect(Collectors.toList());
    }

}

