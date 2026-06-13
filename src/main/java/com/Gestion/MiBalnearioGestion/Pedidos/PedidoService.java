package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.PedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iDetallePedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iEntregaPedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iPedidoRepository;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.MesaRepositorio;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final iPedidoRepository pedidoRepository;
    private final MesaRepositorio mesaRepository;       // de Recursos
    private final ReservaRepository reservaRepository; // de Reservas
    private final DetallePedidoService detallePedidoService;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponse crearPedidoMesa(PedidoMesaDTO dto) {

        MesaEntity mesa = mesaRepository.findByPublicId(dto.getMesaId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada","MesaEntity"));

        PedidoMesaEntity pedido = PedidoMesaEntity.builder()
                .fechaPedido(LocalDate.now())
                .tipoPedido(ETipoPedido.MESA)
                .mesa(mesa)
                .build();

        pedido = (PedidoMesaEntity) pedidoRepository.save(pedido);

        agregarDetalles(pedido, dto.getPedidos());

        return pedidoMapper.convertToResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponse crearPedidoReserva(PedidoReservaDTO dto) {

        ReservaEntity reserva = reservaRepository.findByPublicId(dto.getReservaId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Reserva no encontrada", "ReservaEntity"));

        PedidoReservaEntity pedido = PedidoReservaEntity.builder()
                .fechaPedido(LocalDate.now())
                .tipoPedido(ETipoPedido.RESERVA)
                .reserva(reserva)
                .build();

        pedido = (PedidoReservaEntity) pedidoRepository.save(pedido);

        agregarDetalles(pedido, dto.getPedidos());

        return pedidoMapper.convertToResponseDTO(pedido);
    }

    private void agregarDetalles(PedidoEntity pedido, List<DetallePedidoRequest> detallesRequest) {
        List<DetallePedidoEntity> detalles = detallesRequest.stream()
                .map(req -> detallePedidoService.crearDetallePedido(req, pedido))
                .collect(Collectors.toList());

        pedido.setDetallePedidos(detalles);
    }
}
