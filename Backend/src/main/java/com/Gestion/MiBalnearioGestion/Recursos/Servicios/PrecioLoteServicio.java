package com.Gestion.MiBalnearioGestion.Recursos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.AjustePreciosDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.ResultadoAjustePreciosDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.*;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EOperacionAjuste;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces.IPrecioLoteServicio;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrecioLoteServicio implements IPrecioLoteServicio {

    private static final int MAX_EJEMPLOS = 5;
    private static final double MIN_PORCENTAJE = -90.0;  // tope de seguridad contra errores de tipeo
    private static final double MAX_PORCENTAJE = 300.0;

    private final RecursoRepositorio recursoRepositorio;

    @Transactional // todo o nada: si falla algo, no se guarda ningún cambio
    @Override
    public ResultadoAjustePreciosDTO ajustar(AjustePreciosDTO dto) {
        validar(dto);
        LocalDate hoy = LocalDate.now();

        int recursosAfectados = 0;
        int preciosAfectados = 0;
        List<ResultadoAjustePreciosDTO.Ejemplo> ejemplos = new ArrayList<>();

        for (RecursoEntity recurso : recursoRepositorio.findAll()) {
            if (!coincide(recurso, dto)) continue;

            boolean tocado = false;
            for (PrecioRecursoEntity precio : recurso.getPrecioRecurso()) {
                if (precio.getFechaCaducada().isBefore(hoy)) continue; // los vencidos son historial
                if (dto.getModalidad() != null && precio.getModalidad() != dto.getModalidad()) continue;

                double actual = precio.getPrecio();
                double nuevo = calcular(dto, actual);
                if (nuevo <= 0) {
                    throw new DatosInvalidoException("El ajuste dejaría el precio de " + recurso.getNombre()
                            + " en 0 o menos", "PrecioRecursoEntity");
                }
                if (ejemplos.size() < MAX_EJEMPLOS) {
                    ejemplos.add(new ResultadoAjustePreciosDTO.Ejemplo(
                            recurso.getNombre(), precio.getModalidad(), actual, nuevo));
                }
                if (!dto.isSoloSimular()) {
                    precio.setPrecio(nuevo); // dentro de la transacción: se guarda solo al terminar
                }
                preciosAfectados++;
                tocado = true;
            }
            if (tocado) recursosAfectados++;
        }
        return new ResultadoAjustePreciosDTO(dto.isSoloSimular(), recursosAfectados, preciosAfectados, ejemplos);
    }

    private void validar(AjustePreciosDTO dto) {
        double valor = dto.getValor();
        if (dto.getOperacion() == EOperacionAjuste.PORCENTAJE) {
            if (valor == 0 || valor < MIN_PORCENTAJE || valor > MAX_PORCENTAJE) {
                throw new DatosInvalidoException("El porcentaje debe ser distinto de 0 y estar entre "
                        + MIN_PORCENTAJE + " y " + MAX_PORCENTAJE, "PrecioRecursoEntity");
            }
        } else {
            if (valor <= 0) {
                throw new DatosInvalidoException("El monto debe ser mayor a 0", "PrecioRecursoEntity");
            }
            if (dto.getModalidad() == null) {
                throw new DatosInvalidoException("Para fijar un monto hay que elegir una modalidad", "PrecioRecursoEntity");
            }
        }
    }

    private boolean coincide(RecursoEntity recurso, AjustePreciosDTO dto) {
        Object real = Hibernate.unproxy(recurso);
        boolean tipoOk = switch (dto.getTipo()) {
            case CARPA -> real instanceof CarpaEntity;
            case SOMBRILLA -> real instanceof SombrillaEntity;
            case COCHERA -> real instanceof CocheraEntity;
        };
        return tipoOk && (dto.getSectorPublicId() == null
                || recurso.getSector().getPublicId().equals(dto.getSectorPublicId()));
    }

    private double calcular(AjustePreciosDTO dto, double actual) {
        double nuevo = dto.getOperacion() == EOperacionAjuste.PORCENTAJE
                ? actual * (1 + dto.getValor() / 100.0)
                : dto.getValor();
        return Math.round(nuevo * 100.0) / 100.0;
    }
}