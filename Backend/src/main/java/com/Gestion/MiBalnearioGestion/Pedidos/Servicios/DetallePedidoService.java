package com.Gestion.MiBalnearioGestion.Pedidos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IDetallePedidoService;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ProductoEntity;
import com.Gestion.MiBalnearioGestion.Productos.Repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DetallePedidoService implements IDetallePedidoService {

    private final IDetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    @Override
    public DetallePedidoEntity crearDetallePedido(DetallePedidoRequest request, PedidoEntity pedido) {
        ProductoEntity producto = productoRepository.findByPublicId(request.getProductoId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado: " + request.getProductoId(), "ProductoEntity"));

        if (!producto.getProductoDisponible()) {
            throw new DatosInvalidoException("Lo sentimos. El producto '" + producto.getNombre() + "' no está disponible actualmente", "DetallesPedidoEntity");
        }

        double precioCalculado = producto.getPrecio() * request.getCantidad();

        DetallePedidoEntity detalle = DetallePedidoEntity.builder()
                .publicId(UUID.randomUUID())
                .cantidad(request.getCantidad())
                .precio(precioCalculado)
                .producto(producto)
                .pedido(pedido)
                .build();

        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    @Override
    public void eliminarDetalle(UUID detallePublicId) {
        DetallePedidoEntity detalle = detallePedidoRepository.findByPublicId(detallePublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Línea de pedido no encontrada" + detallePublicId.toString(), "DetallePedidoEntity"));

        PedidoEntity pedido = detalle.getPedido();

        boolean esMesa = pedido.getTipoPedido() == ETipoPedido.MESA;
        boolean editable = esMesa
                ? pedido.getEstadoPedido() == EEstadoPedido.PENDIENTE_PAGO
                : pedido.getEstadoPedido() == EEstadoPedido.ENTREGADO;

        if (!editable) {
            throw new DatosInvalidoException("El pedido no se puede modificar en su estado actual", "DetallesPedidoEntity");
        }

        pedido.getDetallePedidos().remove(detalle);
        detallePedidoRepository.delete(detalle);
    }
}