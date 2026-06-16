package com.Gestion.MiBalnearioGestion.Pedidos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapperDual;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Productos.DTO.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.Entity.ProductoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PedidoMapper implements IMapperDual<PedidoEntity, PedidoRequest, PedidoResponse> {

    @Override
    public PedidoRequest convertToDTO(PedidoEntity entity) {
        if (entity == null) return null;
        return PedidoRequest.builder()
                .tipoPedido(entity.getTipoPedido())
                .build();
    }

    @Override
    public PedidoEntity convertToEntity(PedidoRequest dto, Class<PedidoEntity> entityClass) {
        throw new UnsupportedOperationException("Las entities se construyen directamente en el service."); //esto nunca deberia llegar, si llega es que hicimos algo mal
    }

    public void updateEntityFromDTO(PedidoRequest dto, PedidoEntity entity) {
        if (dto == null || entity == null) return;
        entity.setTipoPedido(dto.getTipoPedido());
    }

    @Override
    public PedidoResponse convertToResponseDTO(PedidoEntity entity) {
        if (entity == null) return null;

        return PedidoResponse.builder()
                .publicId(entity.getPublicId())
                .fechaPedido(entity.getFechaPedido())
                .tipoPedido(entity.getTipoPedido())
                .estadoPedido(entity.getEstadoPedido())
                .detalles(mapearDetalles(entity.getDetallePedidos()))
                .empleadosIds(mapearEmpleadosIds(entity.getEmpleados()))
                .linkPago(null)
                .build();
    }

    private List<DetallePedidoResponse> mapearDetalles(List<DetallePedidoEntity> detalles) {
        if (detalles == null || detalles.isEmpty()) return new ArrayList<>();
        return detalles.stream()
                .map(d -> DetallePedidoResponse.builder()
                        .publicId(d.getPublicId())
                        .cantidad(d.getCantidad())
                        .precio(d.getPrecio())
                        .producto(mapearProducto(d.getProducto()))
                        .build())
                .toList();
    }

    private ProductoDTO mapearProducto(ProductoEntity p) {
        if (p == null) return null;
        ProductoDTO dto = new ProductoDTO();
        dto.setPublicId(p.getPublicId());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setCategoria(p.getCategoria());
        dto.setProductoDisponible(p.getProductoDisponible());
        return dto;
    }

    private List<UUID> mapearEmpleadosIds(List<EmpleadoEntity> empleados) {
        if (empleados == null || empleados.isEmpty()) return new ArrayList<>();
        return empleados.stream()
                .map(EmpleadoEntity::getPublicId)
                .toList();
    }
}