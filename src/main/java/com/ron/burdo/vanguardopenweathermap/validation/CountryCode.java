package com.ron.burdo.vanguardopenweathermap.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
public @interface CountryCode {

    String message() default "Country code ${validatedValue} does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}