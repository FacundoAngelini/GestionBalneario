package com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;

import java.util.UUID;

public interface IDetallePedidoService {
    DetallePedidoEntity crearDetallePedido(DetallePedidoRequest request, PedidoEntity pedido);
    void eliminarDetalle(UUID detallePublicId);
}
