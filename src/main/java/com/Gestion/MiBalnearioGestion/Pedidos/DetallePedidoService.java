package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.ProductoException;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.DetallePedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Productos.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoMapper detallePedidoMapper;
    private final iDetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public DetallePedidoResponse crearDetallePedido(DetallePedidoRequest request) {

        DetallePedidoEntity detallePedido= detallePedidoMapper.convertToEntity(request,DetallePedidoEntity.class);
        detallePedido.setPublicId(UUID.randomUUID());
        detallePedido.getProductos().stream()
                .filter(producto -> !producto.isProductoDisponible())
                .findFirst()
                .ifPresent(productoNoDisponible -> {
                    throw new ProductoException(
                            String.format("El producto '%s' (ID: %s) no está disponible",
                                    productoNoDisponible.getNombre(),
                                    productoNoDisponible.getPublicId())
                    );
                });
        return detallePedidoMapper.convertToResponseDTO(detallePedidoRepository.save(detallePedido));
    }
}
