package com.Gestion.MiBalnearioGestion.Usuarios.Exception;

import java.time.Instant;

public class CuentaNoEncontradaException extends RuntimeException {
    private final String entidad;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public CuentaNoEncontradaException(String s, String entidad) {
        super(s);
        this.entidad = entidad;
        this.campo = null;
        this.valor=null;
        this.timestamp=Instant.now();
    }
}
