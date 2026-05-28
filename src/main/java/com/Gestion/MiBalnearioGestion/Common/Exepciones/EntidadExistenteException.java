package com.Gestion.MiBalnearioGestion.Common.Exepciones;

import jakarta.persistence.EntityExistsException;

import java.time.Instant;

public class EntidadExistenteException extends EntityExistsException {

    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public EntidadExistenteException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }

    public EntidadExistenteException(String mensaje, String entidad, String campo, Object valor, Instant timestamp) {
        super(mensaje);
        this.entidad = entidad;
        this.campo = campo;
        this.valor = valor;
        this.timestamp = Instant.now();
    }
}
