package com.Gestion.MiBalnearioGestion.Pedidos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final iDetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public DetallePedidoEntity crearDetallePedido(DetallePedidoRequest request, PedidoEntity pedido) {
        ProductoEntity producto = productoRepository.findByPublicId(request.getProductoId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado: " + request.getProductoId().toString(), "ProductoEntity"));

        if (!producto.getProductoDisponible()) {
            throw new IllegalStateException("Lo sentimos. El producto '" + producto.getNombre() + "' no está disponible actualmente.");
        }

        DetallePedidoEntity detalle = DetallePedidoEntity.builder()
                .publicId(UUID.randomUUID())
                .cantidad(request.getCantidad())
                .precio(producto.getPrecio() * request.getCantidad())
                .producto(producto)
                .pedido(pedido)
                .build();

        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    public void eliminarDetalle(UUID detallePublicId) {
        DetallePedidoEntity detalle = detallePedidoRepository.findByPublicId(detallePublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Línea de pedido no encontrada", detallePublicId.toString()));

        PedidoEntity pedido = detalle.getPedido();

        if (pedido.getEstadoPedido() != EEstadoPedido.PENDIENTE_PAGO && pedido.getTipoPedido() != ETipoPedido.MESA) {
            throw new IllegalStateException("No se puede eliminar un producto de un pedido que ya fue pagado o está en preparación.");
        }

        pedido.getDetallePedidos().remove(detalle);

        detallePedidoRepository.delete(detalle);
    }
}