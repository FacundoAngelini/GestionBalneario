package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Exception.RecursoOcupadoException;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Common.MercadoPago.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IPagoRepository;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.TemporadaValidator;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Exception.ReservaException;
import com.Gestion.MiBalnearioGestion.Reservas.Mapper.ReservaMapper;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServicio implements IReservaServicio{

    private final ReservaRepository reservaRepository;
    private final ClientesRepository clienteRepository;
    private final RecursoRepositorio recursoRepositorio;
    private final IPagoRepository ipagoRepository;
    private final ReservaMapper reservaMapper;
    private final MercadoPagoService mercadoPagoService;
    private final TemporadaValidator temporadaValidator;
    private final EmailService emailService;
    private final IPagoRepository pagoRepository;

    @Transactional
    @Override
    public ReservaEntity crearReservaInicial(ReservaDTO dto) {
        ClienteEntity cliente = clienteRepository.findByPublicId(dto.getClientePublicId())
                .orElseThrow(() -> new CuentaNoEncontradaException("Cliente no encontrado"+ dto.getClientePublicId().toString(), "ClienteEntity"));

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new DatosInvalidoException("La fecha de fin no puede ser anterior a la de inicio", "ReservaEntity");
        }
        if (dto.getFechaInicio().isBefore(LocalDate.now())) {
            throw new ReservaException("No se pueden realizar reservas para fechas pasadas", "ReservaEntity");
        }
        temporadaValidator.validarFechasEnTemporada(dto.getFechaInicio(), dto.getFechaFin());
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
                throw new RecursoOcupadoException("El recurso " + recurso.getNombre() + " esta desactivado", "RecursoEntity");
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
                .fechaCreacion(LocalDateTime.now())
                .build();
        return reservaRepository.save(reserva);
    }

    @Transactional
    @Override
    public CheckoutResponseDTO crearReservaYGenerarCheckout(ReservaDTO dto) {
        ReservaEntity reserva = this.crearReservaInicial(dto);

        PagoReservaEntity pagoReserva = PagoReservaEntity.builder()
                .monto(reserva.getMontoTotal())
                .eestadoPago(EestadoPago.PENDIENTE)
                .fechaPago(LocalDate.now())
                .metodoPago(MetodoPago.TARJETA)
                .descuento(0.0)
                .reserva(reserva)
                .build();

        PagoReservaEntity pagoGuardado = ipagoRepository.save(pagoReserva);
        reserva.setPagosReservaaa(pagoGuardado);
        reservaRepository.save(reserva);

        MercadoPagoService.PreferenciaMP preferencia = mercadoPagoService.crearPreferenciaPago(
                pagoGuardado.getPublicId(),
                reserva.getMontoTotal(),
                "Reserva Balneario - Codigo: " + reserva.getPublicId().toString().substring(0, 8));
        pagoGuardado.setPreferenceIdMp(preferencia.preferenceId());
        ipagoRepository.save(pagoGuardado);

        String urlMp = preferencia.initPoint();

        emailService.confirmacionReserva(reserva, urlMp)
                .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", reserva.getCliente().getEmail()))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email de confirmación de pago a: {}", reserva.getCliente().getEmail(), throwable);
                    return null;
                });

        return CheckoutResponseDTO.builder()
                .reservaPublicId(reserva.getPublicId())
                .pagoPublicId(pagoGuardado.getPublicId())
                .urlMercadoPago(urlMp)
                .montoTotal(reserva.getMontoTotal())
                .mensaje("Preferencia de pago creada con exito. Complete el pago en el enlace adjunto.")
                .build();
    }


    @Transactional(readOnly = true)
    @Override
    public ReservaDTO buscarPorPublicId(UUID publicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ReservaException("La reserva especificada no existe.", publicId.toString()));
        return reservaMapper.convertToDTO(reserva);
    }


    @Transactional(readOnly = true)
    @Override
    public List<ReservaDTO> listarReservasConFiltros(EReservaEstado estado,
                                                     LocalDate fechaDesde,
                                                     LocalDate fechaHasta,
                                                     UUID clientePublicId){
        PredicateSpecification<ReservaEntity> spec=
                PredicateSpecification.allOf(
                        ReservaSpecification.estadoIgual(estado),
                        ReservaSpecification.fechaInicioDesde(fechaDesde),
                        ReservaSpecification.fechaFinHasta(fechaHasta),
                        ReservaSpecification.clientePublicIdIgual(clientePublicId)
                );
        return reservaRepository.findAll(spec).stream()
                .map(reservaMapper::convertToDTO)
                .toList();
    }

    @Transactional
    @Override
    public void cancelarReservaPorExpiracion(UUID publicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Reserva no encontrada"+ publicId.toString(),"ReservaEntity"));

        PagoReservaEntity pago = reserva.getPagosReservaaa();
        if (pago != null && pago.getPreferenceIdMp() != null) {
            mercadoPagoService.invalidarPreferenciaPago(
                    pago.getPreferenceIdMp(),
                    reserva.getFechaCreacion()
            );
        }

        if (pago != null) {
            ipagoRepository.delete(pago);
        }
        reservaRepository.delete(reserva);
    }

    @Transactional
    @Override
    public void cancelarReservaPorPersonal(UUID reservaPublicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se puede cancelar. La reserva no existe" +
                        reservaPublicId.toString(),"ReservaEntity"));

        if (reserva.getEstadoReserva() == EReservaEstado.CANCELADA) {
            throw new DatosInvalidoException("La reserva ya se encuentra cancelada", "ReservaEntity");
        }
        reserva.setEstadoReserva(EReservaEstado.CANCELADA);
        reserva.setReservado(false);

        if (reserva.getPagosReservaaa() != null) {
            PagoReservaEntity pago = reserva.getPagosReservaaa();

            if (pago.getEestadoPago() == EestadoPago.PAGADO) {
                pago.setEestadoPago(EestadoPago.RECHAZADO);
            } else {
                pago.setEestadoPago(EestadoPago.RECHAZADO);
            }
        }
        reservaRepository.save(reserva);

        emailService.cancelacionReserva(reserva)
                .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", reserva.getCliente().getEmail()))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email de confirmación de pago a: {}", reserva.getCliente().getEmail(), throwable);
                    return null;
                });

    }


    @Transactional
    @Override
    public void cancelarReservaConAnticipacion(CancelarReservaDTO dto) {
        ReservaEntity reserva = reservaRepository.findByPublicId(dto.getPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No se puede cancelar. La reserva no existe"+
                        dto.getPublicId().toString(),"ReservaEntity"));

        if (!reserva.getCliente().getPublicId().equals(dto.getClientePublicId())) {
            throw new DatosInvalidoException("Acceso denegado. Esta reserva no pertenece al cliente informado", "ReservaEntity");
        }

        if (reserva.getEstadoReserva() == EReservaEstado.CANCELADA) {
            throw new DatosInvalidoException("La reserva ya se encuentra cancelada","ReservaEntity");
        }

        if (reserva.getPagosReservaaa() != null &&
                reserva.getPagosReservaaa().getEestadoPago() == EestadoPago.PAGADO) {
            throw new DatosInvalidoException("No se puede cancelar una reserva ya pagada. Contacte al administrador para gestionar el reembolso", "ReservaEntity");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limiteCancelacion = reserva.getFechaInicio().atStartOfDay().minusHours(24);
        if (ahora.isAfter(limiteCancelacion)) {
            throw new DatosInvalidoException("Plazo vencido. Las cancelaciones deben hacerse con al menos 24 horas de anticipación", "ReservaEntity");
        }
        PagoReservaEntity pago = reserva.getPagosReservaaa();
        if (pago != null && pago.getPreferenceIdMp() != null) {
            mercadoPagoService.invalidarPreferenciaPago(
                    pago.getPreferenceIdMp(),
                    reserva.getFechaCreacion()
            );
        }
        emailService.cancelacionReserva(reserva)
                .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", reserva.getCliente().getEmail()))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email de confirmación de pago a: {}", reserva.getCliente().getEmail(), throwable);
                    return null;
                });

        if (pago != null) {
            ipagoRepository.delete(pago);
        }
        reservaRepository.delete(reserva);
    }





    private double obtenerPrecioVigente(RecursoEntity recurso, LocalDate fechaReserva) {
        if (recurso.getPrecioRecurso() == null || recurso.getPrecioRecurso().isEmpty()) {
            throw new EntidadNoEncontradaException("El recurso no tiene ninguna tarifa configurada"+ recurso.getPublicId().toString(), "RecursoEntity");
        }

        return recurso.getPrecioRecurso().stream()
                .filter(precio -> !fechaReserva.isBefore(precio.getFechaVigencia())
                        && !fechaReserva.isAfter(precio.getFechaCaducada()))
                .map(PrecioRecursoEntity::getPrecio)
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una tarifa vigente para la fecha solicitada: " + fechaReserva,
                        recurso.getPublicId().toString()));
    }

}