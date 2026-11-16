package com.Gestion.MiBalnearioGestion.Common.Validacion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CuitValidator implements ConstraintValidator<CuitValido, String> {

    private static final int[] PESOS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    public static boolean esValido(String cuit) {
        if (cuit == null || !cuit.matches("\\d{11}")) return false;
        int suma = 0;
        for (int i = 0; i < 10; i++) suma += (cuit.charAt(i) - '0') * PESOS[i];
        int verificador = 11 - (suma % 11);
        if (verificador == 11) verificador = 0;
        return verificador == (cuit.charAt(10) - '0'); // si da 10, nunca coincide: ese CUIT no existe
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        return valor == null || esValido(valor); // la obligatoriedad la controla @NotBlank
    }
}