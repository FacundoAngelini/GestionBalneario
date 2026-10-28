package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.*;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoRecurso;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecursoLoteServicio implements IRecursoLoteServicio {

    private static final int MAX_POR_LOTE = 200;

    private final ICarpaServicio carpaServicio;
    private final ISombrillaServicio sombrillaServicio;
    private final ICocheraServicio cocheraServicio;
    private final IPrecioRecursoServicio precioServicio;

    @Transactional // todo o nada: si falla uno, se revierten todos
    public int crearLote(RecursoLoteDTO dto) {
        validar(dto);
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
    }
}