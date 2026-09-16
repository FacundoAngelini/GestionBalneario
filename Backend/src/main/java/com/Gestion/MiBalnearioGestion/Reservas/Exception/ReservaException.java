package com.Gestion.MiBalnearioGestion.Reservas.Exception;

import java.time.Instant;

public class ReservaException extends RuntimeException {
    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public ReservaException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }

}
