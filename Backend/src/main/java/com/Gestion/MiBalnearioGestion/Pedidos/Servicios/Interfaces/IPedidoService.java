package com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoLugarDTO;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IPedidoService {
    PedidoResponse buscarPorPublicId(UUID publicId);
    List<PedidoResponse> buscarTodos(ETipoPedido tipoPedido, EEstadoPedido estadoPedido, LocalDate fecha);
    PedidoResponse crearPedidoMesa(PedidoLugarDTO dto);
    DetallePedidoResponse agregarDetalleAMesa(UUID pedidoPublicId, DetallePedidoRequest request);
    void cancelarPedido(UUID publicId);
    void cancelarPedidoPagadoConReembolso(UUID pedidoPublicId);
    PedidoResponse crearPedidoLugarOnline(PedidoLugarDTO dto);
    TicketDTO crearPedidoLugarPresencial(PedidoLugarDTO dto);
}
