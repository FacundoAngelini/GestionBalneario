package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iEntregaPedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iPedidoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final iPedidoRepository pedidoRespository;
    private final iEntregaPedidoRepository entregaPedidoRepository;
    private final iDetallePedidoRepository detallePedidoRepository;

    @Transactional
    public PedidoResponse crearPedido(PedidoRequest pedidoRequest) {

    return null;
    }
}
