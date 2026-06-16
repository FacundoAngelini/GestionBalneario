package com.Gestion.MiBalnearioGestion.Recursos.Exception;

import java.time.Instant;

public class RecursoException extends RuntimeException {
    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public RecursoException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }
}
