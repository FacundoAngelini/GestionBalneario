package com.Gestion.MiBalnearioGestion.Recursos.Exception;

import java.time.Instant;

public class RecursoOcupadoException extends RuntimeException {
    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public RecursoOcupadoException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }

}
