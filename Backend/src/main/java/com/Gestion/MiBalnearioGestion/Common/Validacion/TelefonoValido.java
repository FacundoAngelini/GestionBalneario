package com.Gestion.MiBalnearioGestion.Common.Validacion;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefonoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefonoValido {
    String message() default "El teléfono no es válido: incluí el código de país y solo números (ej: 542235123456)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}