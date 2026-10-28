package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.*;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoRecurso;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CarpaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.CocheraRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.SombrillaRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecursoLoteServicio implements IRecursoLoteServicio {

    private static final int MAX_POR_LOTE = 200;
    private static final int MAX_PASILLO = 20;
    private static final int MIN_CAPACIDAD = 2;
    private static final int MAX_CAPACIDAD = 8;
    private static final int MAX_NOMBRE = 25;
    private static final int MAX_POR_UBICACION = 200; // total por pasillo (carpas) o por sector (sombrillas, cocheras)

    private final ICarpaServicio carpaServicio;
    private final ISombrillaServicio sombrillaServicio;
    private final ICocheraServicio cocheraServicio;
    private final IPrecioRecursoServicio precioServicio;
    private final CarpaRepositorio carpaRepositorio;
    private final SombrillaRepositorio sombrillaRepositorio;
    private final CocheraRepositorio cocheraRepositorio;

    @Transactional // todo o nada: si falla uno, se revierten todos
    public int crearLote(RecursoLoteDTO dto) {
        validar(dto);
        validar(dto);
        validarDisponibilidad(dto);
        int creados = 0;
        for (int n = dto.getNumeroDesde(); n <= dto.getNumeroHasta(); n++) {
            UUID recursoId = crearRecurso(dto, n);
            for (PrecioLoteDTO p : dto.getPrecios()) {
                precioServicio.crearPrecio(PrecioRecursoDTO.builder()
                        .recursoPublicId(recursoId)
                        .modalidad(p.getModalidad())
                        .precio(p.getPrecio())
                        .fechaVigencia(p.getFechaVigencia())
                        .fechaCaducada(p.getFechaCaducada())
                        .build());
            }
            creados++;
        }
        return creados;
    }

    private UUID crearRecurso(RecursoLoteDTO dto, int numero) {
        String nombre = dto.getPrefijoNombre().trim() + " " + numero;
        return switch (dto.getTipo()) {
            case CARPA -> carpaServicio.crearCarpa(CarpaDTO.builder()
                    .nombre(nombre).esReservable(true).sectorPublicId(dto.getSectorPublicId())
                    .numero(numero).pasillo(dto.getPasillo()).capacidad(dto.getCapacidad())
                    .build()).getPublicId();
            case SOMBRILLA -> sombrillaServicio.crearSombrilla(SombrillaDTO.builder()
                    .nombre(nombre).esReservable(true).sectorPublicId(dto.getSectorPublicId())
                    .numero(numero).tamanio(dto.getTamanio())
                    .build()).getPublicId();
            case COCHERA -> cocheraServicio.crearCochera(CocheraDTO.builder()
                    .nombre(nombre).esReservable(true).sectorPublicId(dto.getSectorPublicId())
                    .numeroCochera(numero)
                    .build()).getPublicId();
        };
    }

    private void validar(RecursoLoteDTO dto) {
        if (dto.getNumeroHasta() < dto.getNumeroDesde()) {
            throw new DatosInvalidoException("'Hasta' no puede ser menor que 'desde'", "RecursoEntity");
        }
        if (dto.getNumeroHasta() - dto.getNumeroDesde() + 1 > MAX_POR_LOTE) {
            throw new DatosInvalidoException("Máximo " + MAX_POR_LOTE + " recursos por carga", "RecursoEntity");
        }
        if (dto.getTipo() == ETipoRecurso.CARPA && (dto.getPasillo() == null || dto.getCapacidad() == null)) {
            throw new DatosInvalidoException("Las carpas requieren pasillo y capacidad", "RecursoEntity");
        }
        if (dto.getTipo() == ETipoRecurso.SOMBRILLA && dto.getTamanio() == null) {
            throw new DatosInvalidoException("Las sombrillas requieren tamaño", "RecursoEntity");
        }
        String prefijo = dto.getPrefijoNombre();
        if (prefijo == null || prefijo.isBlank() || prefijo.trim().length() > MAX_NOMBRE) {
            throw new DatosInvalidoException(
                    "El nombre base es obligatorio y admite hasta " + MAX_NOMBRE + " caracteres", "RecursoEntity");
        }
        if (dto.getTipo() == ETipoRecurso.CARPA) {
            if (dto.getPasillo() < 1 || dto.getPasillo() > MAX_PASILLO) {
                throw new DatosInvalidoException("El pasillo debe estar entre 1 y " + MAX_PASILLO, "RecursoEntity");
            }
            if (dto.getCapacidad() < MIN_CAPACIDAD || dto.getCapacidad() > MAX_CAPACIDAD) {
                throw new DatosInvalidoException(
                        "La capacidad debe estar entre " + MIN_CAPACIDAD + " y " + MAX_CAPACIDAD, "RecursoEntity");
            }
        }
        if (dto.getPrecios() == null || dto.getPrecios().isEmpty()) {
            throw new DatosInvalidoException("Debe cargar al menos un precio", "RecursoEntity");
        }
        for (PrecioLoteDTO p : dto.getPrecios()) {
            if (p.getPrecio() == null || p.getPrecio() <= 0) {
                throw new DatosInvalidoException("Los precios deben ser mayores a 0", "RecursoEntity");
            }
            if (p.getFechaCaducada().isBefore(p.getFechaVigencia())) {
                throw new DatosInvalidoException(
                        "La fecha de fin no puede ser anterior a la de inicio", "RecursoEntity");
            }
        }
    }

    private void validarDisponibilidad(RecursoLoteDTO dto) {
        int desde = dto.getNumeroDesde();
        int hasta = dto.getNumeroHasta();
        int nuevos = hasta - desde + 1;

        switch (dto.getTipo()) {
            case CARPA -> {
                if (carpaRepositorio.existsByNumeroBetween(desde, hasta)) {
                    throw new DatosInvalidoException(
                            "Ya existen carpas con números entre " + desde + " y " + hasta, "RecursoEntity");
                }
                long existentes = carpaRepositorio.countByPasillo(dto.getPasillo());
                validarCupo(existentes, nuevos, "el pasillo " + dto.getPasillo(), "carpas");
            }
            case SOMBRILLA -> {
                if (sombrillaRepositorio.existsByNumeroBetween(desde, hasta)) {
                    throw new DatosInvalidoException(
                            "Ya existen sombrillas con números entre " + desde + " y " + hasta, "RecursoEntity");
                }
                long existentes = sombrillaRepositorio.countBySectorPublicId(dto.getSectorPublicId());
                validarCupo(existentes, nuevos, "el sector elegido", "sombrillas");
            }
            case COCHERA -> {
                if (cocheraRepositorio.existsByNumeroCocheraBetween(desde, hasta)) {
                    throw new DatosInvalidoException(
                            "Ya existen cocheras con números entre " + desde + " y " + hasta, "RecursoEntity");
                }
                long existentes = cocheraRepositorio.countBySectorPublicId(dto.getSectorPublicId());
                validarCupo(existentes, nuevos, "el sector elegido", "cocheras");
            }
        }
    }

    private void validarCupo(long existentes, int nuevos, String donde, String cosas) {
        if (existentes + nuevos > MAX_POR_UBICACION) {
            long libres = Math.max(0, MAX_POR_UBICACION - existentes);
            throw new DatosInvalidoException(
                    "En " + donde + " ya hay " + existentes + " " + cosas + " (máximo "
                            + MAX_POR_UBICACION + "). Solo podés cargar " + libres + " más", "RecursoEntity");
        }
    }
}