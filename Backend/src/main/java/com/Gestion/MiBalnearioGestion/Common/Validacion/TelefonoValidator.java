package com.Gestion.MiBalnearioGestion.Common.Validacion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/** Mismas reglas que la tabla de países del frontend: código de país + número local con su largo. */
public class TelefonoValidator implements ConstraintValidator<TelefonoValido, String> {

    private record Pais(String prefijo, int min, int max) {}

    private static final List<Pais> PAISES = List.of(
            new Pais("54", 10, 10),   // Argentina
            new Pais("598", 8, 8),    // Uruguay
            new Pais("56", 9, 9),     // Chile
            new Pais("55", 10, 11),   // Brasil
            new Pais("595", 7, 9),    // Paraguay
            new Pais("34", 9, 9));    // España

    public static boolean esValido(String telefono) {
        if (telefono == null || !telefono.matches("\\d+")) return false;
        return PAISES.stream().anyMatch(p -> {
            int local = telefono.length() - p.prefijo().length();
            return telefono.startsWith(p.prefijo()) && local >= p.min() && local <= p.max();
        });
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        return valor == null || esValido(valor); // la obligatoriedad la controla @NotBlank
    }
}