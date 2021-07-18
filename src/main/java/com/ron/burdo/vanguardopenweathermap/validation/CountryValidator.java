package com.ron.burdo.vanguardopenweathermap.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;


public class CountryValidator implements ConstraintValidator<CountryCode, String> {
    private final Set<String> countryCodes;

    public CountryValidator() {
        countryCodes = Set.of(Locale.getISOCountries());
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext constraintValidatorContext) {
        return countryCodes.contains(country.toUpperCase());
    }

}
