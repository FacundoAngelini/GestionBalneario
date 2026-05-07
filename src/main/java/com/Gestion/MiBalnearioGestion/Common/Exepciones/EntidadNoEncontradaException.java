package com.Gestion.MiBalnearioGestion.Common.Exepciones;

import java.time.Instant;
import java.util.NoSuchElementException;

public class EntidadNoEncontradaException extends NoSuchElementException {

    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public EntidadNoEncontradaException(String mensaje, String entidad, String campo, Object valor, Instant timestamp) {
        super(mensaje);
        this.entidad = entidad;
        this.campo = campo;
        this.valor = valor;
        this.timestamp = timestamp;
    }
}
