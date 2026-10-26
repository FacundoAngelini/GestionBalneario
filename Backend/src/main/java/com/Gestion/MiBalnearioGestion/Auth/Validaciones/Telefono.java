package com.Gestion.MiBalnearioGestion.Auth.Validaciones;

public final class Telefono {
    // Código de país + número, solo dígitos:
    // AR(54) 10, UY(598) 8, CL(56) 9, BR(55) 10-11, PY(595) 7-9, ES(34) 9
    public static final String REGEX =
            "^(54[0-9]{10}|598[0-9]{8}|56[0-9]{9}|55[0-9]{10,11}|595[0-9]{7,9}|34[0-9]{9})$";

    public static final String MENSAJE =
            "El teléfono debe incluir el código de país y un número válido (solo dígitos, sin +)";
}
