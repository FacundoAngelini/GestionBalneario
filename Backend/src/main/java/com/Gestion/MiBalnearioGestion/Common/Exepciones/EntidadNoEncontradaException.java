package com.Gestion.MiBalnearioGestion.Common.Exepciones;

import java.time.Instant;
import java.util.NoSuchElementException;

public class EntidadNoEncontradaException extends NoSuchElementException {

    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public EntidadNoEncontradaException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }

}
