package com.Gestion.MiBalnearioGestion.Common.Exepciones;

import java.time.Instant;

public class ProductoException extends RuntimeException {
    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public ProductoException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }
}
