package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Exception.RecursoOcupadoException;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Mapper.ReservaMapper;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
    @RequiredArgsConstructor
    public class ReservaServicio {

        private final ReservaRepository reservaRepository;
        private final ClientesRepository clienteRepository;
        private final RecursoRepositorio recursoRepositorio;
        private final iPagoRepository ipagoRepository;
        private final ReservaMapper reservaMapper;
        private final MercadoPagoService mercadoPagoService;

    @Transactional
    public ReservaEntity crearReservaInicial(ReservaDTO dto) {
        ClienteEntity cliente = clienteRepository.findByPublicId(dto.getClientePublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado", dto.getClientePublicId().toString()));

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (dto.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden realizar reservas para fechas pasadas");
        }
        validarLimitesTemporada(dto.getFechaInicio(), dto.getFechaFin());
        long diasEstadia = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin()) + 1;
        double montoTotal = 0.0;
        List<RecursoEntity> recursosEntities = new ArrayList<>();
        List<EReservaEstado> estadosConflictivos = List.of(EReservaEstado.PENDIENTE, EReservaEstado.CONFIRMADA);

        for (UUID recursoId : dto.getRecursosPublicIds()) {
            if (reservaRepository.isRecursoOcupadoEnFechas(recursoId, dto.getFechaInicio(), dto.getFechaFin(), estadosConflictivos)) {
                throw new RecursoOcupadoException("El recurso con ID " + recursoId + " ya esta ocupado", "RecursoEntity");
            }

            RecursoEntity recurso = recursoRepositorio.findByPublicIdWithPrecios(recursoId)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Recurso no encontrado", recursoId.toString()));

            if (!recurso.isEsReservable()) {
                throw new RuntimeException("El recurso " + recurso.getNombre() + " esta desactivado.");
            }

            montoTotal += (obtenerPrecioVigente(recurso, dto.getFechaInicio()) * diasEstadia);
            recursosEntities.add(recurso);
        }

        ReservaEntity reserva = ReservaEntity.builder()
                .publicId(UUID.randomUUID())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .reservado(false)
                .estadoReserva(EReservaEstado.PENDIENTE)
                .montoTotal(montoTotal)
                .cliente(cliente)
                .recursos(recursosEntities)
                .build();

        return reservaRepository.save(reserva);
    }

    @Transactional
    public CheckoutResponseDTO crearReservaYGenerarCheckout(ReservaDTO dto) {
        ReservaEntity reserva = this.crearReservaInicial(dto);
        PagoReservaEntity pagoReserva = PagoReservaEntity.builder()
                .monto(reserva.getMontoTotal())
                .eestadoPago(EestadoPago.PENDIENTE)
                .fechaPago(LocalDate.now())
                .metodoPago(MetodoPago.TARJETA)
                .descuento(0.0)
                .reserva(reserva) // ESTO LE ASIGNA LA RESERVA AL PAGO
                .build();
        PagoReservaEntity pagoGuardado = ipagoRepository.save(pagoReserva);
        reserva.setPagosReservaaa(pagoGuardado);
        reservaRepository.save(reserva);
        String urlMp = mercadoPagoService.crearPreferenciaPago(
                pagoGuardado.getPublicId(),
                reserva.getMontoTotal(),
                "Reserva Balneario - Codigo: " + reserva.getPublicId().toString().substring(0, 8)
        );
        return CheckoutResponseDTO.builder()
                .reservaPublicId(reserva.getPublicId())
                .pagoPublicId(pagoGuardado.getPublicId())
                .urlMercadoPago(urlMp)
                .montoTotal(reserva.getMontoTotal())
                .mensaje("Preferencia de pago creada con exito. Complete el pago en el enlace adjunto.")
                .build();
    }

    @Transactional(readOnly = true)
    public ReservaDTO buscarPorPublicId(UUID publicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("La reserva especificada no existe.", publicId.toString()));
        return reservaMapper.convertToDTO(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::convertToDTO)
                .toList();
    }

    @Transactional
    public void cancelarReservaConAnticipacion(CancelarReservaDTO dto) {
        ReservaEntity reserva = reservaRepository.findByPublicId(dto.getPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se puede cancelar. La reserva no existe",
                        dto.getPublicId().toString()
                ));
        if (!reserva.getCliente().getPublicId().equals(dto.getClientePublicId())) {
            throw new RuntimeException("Acceso denegado. Esta reserva no pertenece al cliente informado");
        }
        if (reserva.getEstadoReserva() == EReservaEstado.CANCELADA) {
            throw new RuntimeException("La reserva ya se encuentra cancelada");
        }
        if (reserva.getPagosReservaaa() != null &&
                reserva.getPagosReservaaa().getEestadoPago() == EestadoPago.PAGADO) {
            throw new RuntimeException("No se puede cancelar una reserva ya pagada. Contacte al administrador para gestionar el reembolso");
        }
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limiteCancelacion = reserva.getFechaInicio().atStartOfDay().minusHours(24);

        if (ahora.isAfter(limiteCancelacion)) {
            throw new RuntimeException("Plazo vencido. Las cancelaciones deben hacerse con al menos 24 horas de anticipacion");
        }

        reserva.setEstadoReserva(EReservaEstado.CANCELADA);
        reserva.setReservado(false);

        if (reserva.getPagosReservaaa() != null) {
            reserva.getPagosReservaaa().setEestadoPago(EestadoPago.RECHAZADO);
        }

        reservaRepository.save(reserva);
        System.out.println("Reserva " + dto.getClientePublicId() + " cancelada correctamente");
    }


    private void validarLimitesTemporada(LocalDate inicio, LocalDate fin) {
        if (inicio.getMonthValue() > 4 && inicio.getMonthValue() < 12) {
            throw new IllegalArgumentException("Fuera de temporada. El balneario opera del 1 de Diciembre al 15 de Abril.");
        }
        if (fin.getMonthValue() == 4 && fin.getDayOfMonth() > 15) {
            throw new IllegalArgumentException("La temporada finaliza estrictamente el 15 de Abril.");
        }
    }


    private double obtenerPrecioVigente(RecursoEntity recurso, LocalDate fechaReserva) {
        if (recurso.getPrecioRecurso() == null || recurso.getPrecioRecurso().isEmpty()) {
            throw new EntidadNoEncontradaException("El recurso no tiene ninguna tarifa configurada", recurso.getPublicId().toString());
        }

        return recurso.getPrecioRecurso().stream()
                .filter(precio -> !fechaReserva.isBefore(precio.getFechaVigencia())
                        && !fechaReserva.isAfter(precio.getFechaCaducada()))
                .map(PrecioRecursoEntity::getPrecio)
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No existe una tarifa vigente para la fecha solicitada: " + fechaReserva,
                        recurso.getPublicId().toString()
                ));
    }

    }