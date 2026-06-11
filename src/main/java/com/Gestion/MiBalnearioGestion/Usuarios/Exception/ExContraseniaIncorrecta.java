package com.Gestion.MiBalnearioGestion.Usuarios.Exception;

import java.time.Instant;

public class ExContraseniaIncorrecta extends IllegalArgumentException { ;
    private final String Entity;
    private final String campo;
    private final Object valor;
    private final Instant timestamp;

    public ExContraseniaIncorrecta(String message, String Entity) {
        super(message);
        this.Entity = Entity;
        this.campo = null;
        this.valor = null;
        this.timestamp = Instant.now();
    }
}
