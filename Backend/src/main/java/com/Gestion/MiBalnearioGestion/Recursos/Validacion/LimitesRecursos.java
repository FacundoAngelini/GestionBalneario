package com.Gestion.MiBalnearioGestion.Recursos.Validacion;

/** Límites de los recursos. Los usan la edición individual y la carga masiva. */
public final class LimitesRecursos {
    private LimitesRecursos() {}

    public static final int MAX_PASILLO = 20;
    public static final int MIN_CAPACIDAD = 2;
    public static final int MAX_CAPACIDAD = 8;
    public static final int MAX_NUMERO = 1_000;
    public static final int MAX_NOMBRE_BASE = 25;
    public static final int MAX_NOMBRE = 31; // nombre base (25) + un espacio + hasta 5 dígitos
}