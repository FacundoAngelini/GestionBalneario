package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.AccionInvalidaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoCatalogoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.*;
import com.Gestion.MiBalnearioGestion.Recursos.Mappers.RecursoMapper;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;

import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IRecursoServicio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Specification.RecursoSpecification;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.AcceptPendingException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RecursoServicio implements IRecursoServicio {
    private final RecursoRepositorio recursoRepositorio;
    private final RecursoMapper recursoMapper;
    private final TemporadaValidator temporadaValidator;


    @Transactional(readOnly = true)
    @Override
    public RecursoDTO buscarPorPublicId(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro el recurso con el UUID especificado","RecursoEntity"));
        return recursoMapper.convertToDTO(recurso);
    }

    @Transactional
    @Override
    public void desactivarRecurso(UUID publicId) {
        RecursoEntity recurso = recursoRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el recurso a desactivar", "RecursoEntity"));
        recurso.setEsReservable(false);
        recursoRepositorio.save(recurso);
    }

    @Transactional
    @Override
    public void desactivarTodoElInventario() {
        recursoRepositorio.desactivarTodoElInventario();
    }

    @Override
    public void borrarTodoElInventario() {
        long activos = recursoRepositorio.countByEsReservableTrue();

        if (activos > 0) {
            throw new AccionInvalidaException("Operación rechazada.No podés borrar el inventario porque existen "
                    + activos + " recursos activos. Primero debés pasarlos a no disponibles", "RecursoEntity");
        }
        recursoRepositorio.deleteAll();
    }


    @Transactional(readOnly = true)
    @Override
    public List<RecursoDTO> buscarTodos(String nombreIgual,
                                        String nombreContiene,
                                        Boolean reservableVerdad){

        PredicateSpecification<RecursoEntity> spec =
                PredicateSpecification.allOf(
                        RecursoSpecification.nombreContiene(nombreContiene),
                        RecursoSpecification.nombreIgual(nombreIgual),
                        RecursoSpecification.reservableVerdad(reservableVerdad)
                );


        return recursoRepositorio.findAll(spec)
                .stream()
                .map(recursoMapper::convertToDTO)
                .toList();
    }

   @Transactional
    @Override
    public void borrarRecurso(UUID IdPublico) {
        RecursoEntity buscado = recursoRepositorio
                .findByPublicId(IdPublico)
                .orElseThrow(()->new EntidadNoEncontradaException("Recurso no encontrado : "+ IdPublico.toString(), "RecursoEntity"));
        recursoRepositorio.delete(buscado);
        System.out.println("Recurso eliminado con exito");
    }


    @Override
    @Transactional(readOnly = true)
    public List<RecursoDTO> listarDisponiblesParaElCliente(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new DatosInvalidoException("No se pueden buscar recursos para fechas pasadas", "RecursoEntity");
        }
        temporadaValidator.validarFechasEnTemporada(fechaInicio, fechaFin);

        List<EReservaEstado> estadosConflictivos = List.of(EReservaEstado.PENDIENTE, EReservaEstado.CONFIRMADA);
        List<RecursoEntity> disponibles = recursoRepositorio.encontrarDisponiblesEnRango(fechaInicio, fechaFin, estadosConflictivos);

        return disponibles.stream()
                .map(recursoMapper::convertToDTO)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<RecursoCatalogoDTO> listarCatalogo(LocalDate fechaInicio, LocalDate fechaFin, String tipo) {
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new DatosInvalidoException("No se pueden buscar recursos para fechas pasadas", "RecursoEntity");
        }
        temporadaValidator.validarFechasEnTemporada(fechaInicio, fechaFin);

        return recursoRepositorio
                .encontrarDisponiblesEnRango(fechaInicio, fechaFin,
                        List.of(EReservaEstado.PENDIENTE, EReservaEstado.CONFIRMADA))
                .stream()
                .map(this::aCatalogo)
                .filter(r -> r != null && (tipo == null || r.getTipo().equalsIgnoreCase(tipo)))
                .toList();
    }

    private RecursoCatalogoDTO aCatalogo(RecursoEntity r) {
        var b = RecursoCatalogoDTO.builder()
                .publicId(r.getPublicId())
                .nombre(r.getNombre())
                .sectorPublicId(r.getSector().getPublicId())
                .sectorNombre(r.getSector().getNombre())
                .precios(r.getPrecioRecurso().stream()
                        .filter(p -> !p.getFechaCaducada().isBefore(LocalDate.now()))
                        .map(p -> new RecursoCatalogoDTO.PrecioCatalogoDTO(
                                p.getModalidad(), p.getPrecio(), p.getFechaVigencia(), p.getFechaCaducada()))
                        .toList());

        if (r instanceof CarpaEntity c) {
            b.tipo("CARPA").numero(c.getNumero()).pasillo(c.getPasillo()).capacidad(c.getCapacidad());
        } else if (r instanceof SombrillaEntity s) {
            b.tipo("SOMBRILLA").numero(s.getNumero()).tamanio(s.getTamanio().name());
        } else if (r instanceof CocheraEntity c) {
            b.tipo("COCHERA").numero(c.getNumeroCochera());
        } else {
            return null; // mesas, canchas, piletas: no forman parte del catálogo de alquiler
        }
        return b.build();
    }

}
