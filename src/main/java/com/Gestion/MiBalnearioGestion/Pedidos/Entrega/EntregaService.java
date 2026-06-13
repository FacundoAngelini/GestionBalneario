package com.Gestion.MiBalnearioGestion.Pedidos.Entrega;

import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.PedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iEntregaPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaMapper entregaMapper;
    private final EmpleadoMapper empleadoMapper;
    private final PedidoMapper pedidoMapper;
    private final iEntregaPedidoRepository entregaRespository;

    // public EntregaDTO create ()
}
