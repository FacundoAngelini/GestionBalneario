package com.Gestion.MiBalnearioGestion.Pedidos.Servicios;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.AccionInvalidaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Common.MercadoPago.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.ITicketRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.ETipoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.DetallePedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Mappers.PedidoMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IPedidoRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IDetallePedidoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Servicios.Interfaces.IPedidoService;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CarpaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.MesaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.SombrillaRepositorio;


import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PedidoService implements IPedidoService {

    private final IPedidoRepository pedidoRepository;
    private final MesaRepositorio mesaRepository;
    private final CarpaRepositorio carpaRepository;
    private final SombrillaRepositorio sombrillaRepository;
    private final ClientesRepository clienteRepository;
    private final IDetallePedidoService detallePedidoService;
    private final PedidoMapper pedidoMapper;
    private final MercadoPagoService mercadoPagoService;
    private final IPagoRepository pagoRepository;
    private final EmailService emailService;
    private final EmpleadosRepositorio empleadosRepositorio;
    private final ITicketRepository ticketRepository;
    private final DetallePedidoMapper detallePedidoMapper;

    @Transactional(readOnly = true)
    @Override
    public PedidoResponse buscarPorPublicId(UUID publicId) {
        return pedidoRepository.findByPublicId(publicId)
                .map(pedidoMapper::convertToResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no encontrado" + publicId.toString(), "PedidoEntity"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PedidoResponse> buscarTodos(ETipoPedido tipoPedido, EEstadoPedido estadoPedido, LocalDate fecha) {
       PredicateSpecification<PedidoEntity> spec =
               PredicateSpecification.allOf(
                       PedidoSpecification.tipoIgual(tipoPedido),
                       PedidoSpecification.estadoIgual(estadoPedido),
                       PedidoSpecification.fechaIgual(fecha)
               );

        return pedidoRepository.findAll(spec).stream()
                .map(pedidoMapper::convertToResponseDTO)
                .toList();
    }



    @Transactional
    @Override
    public PedidoResponse crearPedidoMesa(PedidoLugarDTO dto) {
        MesaEntity mesa = mesaRepository.findByPublicId(dto.getRecursoPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada", dto.getRecursoPublicId().toString()));

        ClienteEntity cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findByPublicId(dto.getClienteId()).orElse(null);
        }

        PedidoLugarEntity pedido = PedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .pedido(mesa)
                .cliente(cliente)
                .fechaPedido(LocalDate.now())
                .fechaCreacion(LocalDateTime.now())
                .tipoPedido(ETipoPedido.MESA)
                .estadoPedido(EEstadoPedido.PENDIENTE_PAGO) // Listo para la cocina
                .detallePedidos(new ArrayList<>())
                .build();

        pedido = pedidoRepository.save(pedido);
        agregarDetalles(pedido, dto.getPedidos());

        return pedidoMapper.convertToResponseDTO(pedidoRepository.save(pedido));
    }



    @Transactional
    @Override
    public DetallePedidoResponse agregarDetalleAMesa(UUID pedidoPublicId, DetallePedidoRequest request) {
        PedidoEntity pedido = pedidoRepository.findByPublicId(pedidoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no encontrado", pedidoPublicId.toString()));

        if (pedido.getTipoPedido() != ETipoPedido.MESA) {
            throw new DatosInvalidoException("Para pedidos online no se pueden agregar ítems individuales. Debe cancelar y recrear el pedido.", "PedidoEntity");
        }

        if (pedido.getEstadoPedido() == EEstadoPedido.CONFIRMADO) {
            throw new DatosInvalidoException("El pedido ya fue pagado y verificado por caja. No se pueden añadir más productos", "PedidoEntity");
        }

        DetallePedidoEntity detalle = detallePedidoService.crearDetallePedido(request, pedido);
        pedido.getDetallePedidos().add(detalle);
        pedidoRepository.save(pedido);

        return detallePedidoMapper.convertToResponseDTO(detalle);
    }


    @Transactional
    @Override
    public void cancelarPedido(UUID publicId) {
        PedidoEntity pedido = pedidoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no encontrado", publicId.toString()));

        if (pedido.getEstadoPedido() == EEstadoPedido.CONFIRMADO) {
            throw new DatosInvalidoException("No puedes cancelar un pedido que ya fue pagado. Por favor, acércate a la caja", "PedidoEntity");
        }
        if (pedido.getEstadoPedido() == EEstadoPedido.CANCELADO) {
            throw new DatosInvalidoException("El pedido ya se encuentra cancelado", "PedidoEntity");
        }

        pedido.setEstadoPedido(EEstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

     //invalidamos el link de mp
        pagoRepository.findPagoPorPedidoLugar(pedido.getPublicId()).ifPresent(pago -> {
            if (pago instanceof PagoPedidoLugarEntity pagoLugar && pagoLugar.getPreferenceIdMp() != null) {
                mercadoPagoService.invalidarPreferenciaPago(pagoLugar.getPreferenceIdMp(), pedido.getFechaCreacion());
                pagoLugar.setEestadoPago(EestadoPago.RECHAZADO);
                pagoRepository.save(pagoLugar);
            }
        });
    }

 //esto si esta pagado
    @Transactional
    @Override
    public void cancelarPedidoPagadoConReembolso(UUID pedidoPublicId) {
        PedidoEntity pedido = pedidoRepository.findByPublicId(pedidoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no encontrado", pedidoPublicId.toString()));

        if (pedido.getEstadoPedido() == EEstadoPedido.CANCELADO) {
            throw new DatosInvalidoException("Este pedido ya fue cancelado previamente.", "PedidoEntity");
        }

        if (pedido.getEstadoPedido() != EEstadoPedido.CONFIRMADO) {
            throw new AccionInvalidaException("Este método es solo para pedidos pagados de forma efectiva o confirmados.", "PedidoEntity");
        }

        pedido.setEstadoPedido(EEstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        pagoRepository.findPagoPorPedidoLugar(pedido.getPublicId()).ifPresent(pago -> {
            if (pago instanceof PagoPedidoLugarEntity pagoLugar) {
                pagoLugar.setEestadoPago(EestadoPago.RECHAZADO);
                pagoRepository.save(pagoLugar);
            }
        });
    }

    private RecursoEntity resolverRecurso(PedidoLugarDTO dto) {
        return switch (dto.getTipoPedido()) {
            case CARPA -> carpaRepository.findByPublicId(dto.getRecursoPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Carpa no encontrada", dto.getRecursoPublicId().toString()));
            case SOMBRILLA -> sombrillaRepository.findByPublicId(dto.getRecursoPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sombrilla no encontrada", dto.getRecursoPublicId().toString()));
            default -> throw new DatosInvalidoException("Tipo inválido para pedido online: " + dto.getTipoPedido(), "PedidoEntity");
        };
    }

    private void agregarDetalles(PedidoEntity pedido, List<DetallePedidoRequest> detallesRequest) {
        if (detallesRequest == null || detallesRequest.isEmpty()) return;
        for (DetallePedidoRequest req : detallesRequest) {
            DetallePedidoEntity detalle = detallePedidoService.crearDetallePedido(req, pedido);
            pedido.getDetallePedidos().add(detalle);
        }
    }


    @Transactional
    @Override
    public PedidoResponse crearPedidoLugarOnline(PedidoLugarDTO dto) {

        ClienteEntity cliente = clienteRepository.findByPublicId(dto.getClienteId())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cliente no encontrado", dto.getClienteId().toString()));

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

        double monto = calcularTotal(pedido);

        PagoPedidoLugarEntity pago = PagoPedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .monto(monto)
                .eestadoPago(EestadoPago.PENDIENTE)
                .fechaPago(LocalDate.now())
                .metodoPago(MetodoPago.MERCADO_PAGO)
                .pedido(pedido)
                .build();
        pagoRepository.save(pago);

        MercadoPagoService.PreferenciaMP preferencia = mercadoPagoService
                .crearPreferenciaPago(pago.getPublicId(), monto,
                        "Balneario - Pedido #" + pedido.getPublicId().toString().substring(0, 8));

        pago.setPreferenceIdMp(preferencia.preferenceId());
        pagoRepository.save(pago);


        emailService.enviarLinkPagoPedido(pedido, pago, preferencia.initPoint())
                .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", cliente.getEmail()))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email de confirmación de pago a: {}", cliente.getEmail(), throwable);
                    return null;
                });

        PedidoResponse response = pedidoMapper.convertToResponseDTO(pedido);
        response.setLinkPago(preferencia.initPoint());
        return response;
    }

    @Transactional
    @Override
    public TicketDTO crearPedidoLugarPresencial(PedidoLugarDTO dto) {

        EmpleadoEntity cajero = empleadosRepositorio.findByPublicId(dto.getEmpleadoId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no encontrado", dto.getEmpleadoId().toString()));

        RecursoEntity recurso = resolverRecurso(dto);

        PedidoLugarEntity pedido = PedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .pedido(recurso)
                .fechaPedido(LocalDate.now())
                .fechaCreacion(LocalDateTime.now())
                .tipoPedido(dto.getTipoPedido())
                .estadoPedido(EEstadoPedido.CONFIRMADO)
                .detallePedidos(new ArrayList<>())
                .build();

        pedido = pedidoRepository.save(pedido);
        agregarDetalles(pedido, dto.getPedidos());

        double monto = calcularTotal(pedido);

        PagoPedidoLugarEntity pago = PagoPedidoLugarEntity.builder()
                .publicId(UUID.randomUUID())
                .monto(monto)
                .eestadoPago(EestadoPago.PAGADO)
                .fechaPago(LocalDate.now())
                .metodoPago(dto.getMetodoPago())
                .pedido(pedido)
                .build();
        pagoRepository.save(pago);

        TicketEntity ticket = ticketRepository.save(TicketEntity.builder()
                .publicId(UUID.randomUUID())
                .fechaTicket(LocalDateTime.now())
                .total(monto)
                .pagoEntity(pago)
                .empleado(cajero)
                .build());

        return TicketDTO.builder()
                .publicId(ticket.getPublicId())
                .fechaTicket(ticket.getFechaTicket())
                .total(ticket.getTotal())
                .empleadoPublicId(cajero.getPublicId())
                .pagoPublicId(pago.getPublicId())
                .pedidoPublicId(pedido.getPublicId())
                .build();
    }

    private double calcularTotal(PedidoEntity pedido) {
        return pedido.getDetallePedidos().stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();
    }
}

