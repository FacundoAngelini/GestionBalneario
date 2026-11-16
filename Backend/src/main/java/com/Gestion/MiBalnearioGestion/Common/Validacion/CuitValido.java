package com.Gestion.MiBalnearioGestion.Common.Validacion;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CuitValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CuitValido {
    String message() default "El CUIT no es válido: son 11 números y el último es el dígito verificador";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}