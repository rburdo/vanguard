package com.ron.burdo.vanguardopenweathermap.validation;


import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class CountryValidatorTest {

    @Test
    public void testCountryValidator() {
        CountryValidator countryValidator = new CountryValidator();
        countryValidator.initialize(null);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(countryValidator.isValid("au", context));
        assertTrue(countryValidator.isValid("Au", context));
        assertTrue(countryValidator.isValid("AU", context));
        // Should not work for states
        assertFalse(countryValidator.isValid("VIC", context));
        assertFalse(countryValidator.isValid("XY", context));
    }

}
