package com.Gestion.MiBalnearioGestion.Pedidos.Servicios;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.PedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iPedidoRepository;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CarpaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.MesaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.SombrillaRepositorio;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final iPedidoRepository pedidoRepository;
    private final MesaRepositorio mesaRepository;
    private final CarpaRepositorio carpaRepository;
    private final SombrillaRepositorio sombrillaRepository;
    private final ClientesRepository clienteRepository;
    private final DetallePedidoService detallePedidoService;
    private final PedidoMapper pedidoMapper;
    private final MercadoPagoService mercadoPagoService;
    private final iPagoRepository pagoRepository;
    private final EmailService emailService;


    @Transactional(readOnly = true)
    public List<PedidoResponse> buscarTodosConFiltros(ETipoPedido tipo,
                                                      EEstadoPedido estado,
                                                      LocalDate fecha) {
        PredicateSpecification<PedidoEntity> spec =
                PredicateSpecification.allOf(
                        PedidoSpecification.tipoIgual(tipo),
                        PedidoSpecification.estadoIgual(estado),
                        PedidoSpecification.fechaIgual(fecha)
                );
        return pedidoRepository.findAll(spec).stream()
                .map(pedidoMapper::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorPublicId(UUID publicId) {
        return pedidoRepository.findByPublicId(publicId)
                .map(pedidoMapper::convertToResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Pedido no encontrado", publicId.toString()));
    }

    @Transactional
    public PedidoResponse crearPedidoMesa(PedidoMesaDTO dto) {
        MesaEntity mesa = mesaRepository.findByPublicId(dto.getMesaId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Mesa no encontrada", dto.getMesaId().toString()));

        PedidoMesaEntity pedido = PedidoMesaEntity.builder()
                .publicId(UUID.randomUUID())
                .fechaPedido(LocalDate.now())
                .fechaCreacion(LocalDateTime.now())
                .tipoPedido(ETipoPedido.MESA)
                .estadoPedido(EEstadoPedido.CONFIRMADO)
                .mesa(mesa)
                .detallePedidos(new ArrayList<>())
                .build();

        pedido = pedidoRepository.save(pedido);
        agregarDetalles(pedido, dto.getPedidos());

        // Los pedidos de mesa no tienen link de pago — el cajero cobra en persona
        return pedidoMapper.convertToResponseDTO(pedido);
    }


    @Transactional
    public PedidoResponse crearPedidoLugarOnline(PedidoLugarDTO dto) {

        ClienteEntity cliente = clienteRepository.findByPublicId(dto.getClienteId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado", dto.getClienteId().toString()));

        RecursoEntity recurso = resolverRecurso(dto);

        PedidoLugarEntity pedido = PedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .pedido(recurso)
                .cliente(cliente)
                .fechaPedido(LocalDate.now())
                .fechaCreacion(LocalDateTime.now())
                .tipoPedido(dto.getTipoPedido())
                .estadoPedido(EEstadoPedido.PENDIENTE_PAGO)
                .detallePedidos(new ArrayList<>())
                .build();

        pedido = pedidoRepository.save(pedido);
        agregarDetalles(pedido, dto.getPedidos());

        double montoTotal = pedido.getDetallePedidos().stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();

        PagoPedidoLugarEntity pago = PagoPedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .monto(montoTotal)
                .eestadoPago(EestadoPago.PENDIENTE)
                .fechaPago(LocalDate.now())
                .metodoPago(MetodoPago.MERCADO_PAGO)
                .pedido(pedido)
                .build();

        pagoRepository.save(pago);

        String linkPago = mercadoPagoService.crearPreferenciaPago(
                pago.getPublicId(),
                montoTotal,
                "Balneario Gastronomía - Pedido #" + pedido.getPublicId().toString().substring(0, 8)
        );

        emailService.enviarLinkPagoPedido(pedido, pago, linkPago);

        PedidoResponse response = pedidoMapper.convertToResponseDTO(pedido);
        response.setLinkPago(linkPago);
        return response;
    }

    @Transactional
    public void cancelarPedido(UUID publicId) {
        PedidoEntity pedido = pedidoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Pedido no encontrado", publicId.toString()));

        if (pedido.getEstadoPedido() == EEstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido ya está cancelado.");
        }
        if (pedido.getEstadoPedido() == EEstadoPedido.CONFIRMADO) {
            throw new IllegalStateException(
                    "No se puede cancelar un pedido que ya fue pagado y está en preparación.");
        }

        pedido.setEstadoPedido(EEstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }


    private RecursoEntity resolverRecurso(PedidoLugarDTO dto) {
        return switch (dto.getTipoPedido()) {
            case CARPA -> carpaRepository.findByPublicId(dto.getRecursoPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "Carpa no encontrada", dto.getRecursoPublicId().toString()));
            case SOMBRILLA -> sombrillaRepository.findByPublicId(dto.getRecursoPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "Sombrilla no encontrada", dto.getRecursoPublicId().toString()));
            default -> throw new IllegalArgumentException(
                    "Tipo inválido para pedido online: " + dto.getTipoPedido()
                            + ". Solo se permite CARPA o SOMBRILLA.");
        };
    }

    private void agregarDetalles(PedidoEntity pedido, List<DetallePedidoRequest> detallesRequest) {
        if (detallesRequest == null || detallesRequest.isEmpty()) return;
        for (DetallePedidoRequest req : detallesRequest) {
            DetallePedidoEntity detalle = detallePedidoService.crearDetallePedido(req, pedido);
            pedido.getDetallePedidos().add(detalle);
        }
    }
}