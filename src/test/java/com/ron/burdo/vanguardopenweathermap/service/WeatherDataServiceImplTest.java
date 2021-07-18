package com.ron.burdo.vanguardopenweathermap.service;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import com.ron.burdo.vanguardopenweathermap.jpa.WeatherData;
import com.ron.burdo.vanguardopenweathermap.jpa.WeatherDataRepository;
import com.ron.burdo.vanguardopenweathermap.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WeatherDataServiceImplTest {
    private WeatherDataService weatherDataService;

    @Mock
    private OpenWeatherMapProperties properties;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    private final ResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.getResponseMappings()).thenReturn(
                Map.of("weatherDescription", "$.weather[0].description"));

        weatherDataService = new WeatherDataServiceImpl(weatherDataRepository, properties);
    }

    @Test
    public void testAddWeatherData_succeeds() throws Exception {
        String json = readJson("valid");
        weatherDataService.addWeatherData("uk", "London", "12345", json);

       verify(weatherDataRepository).save(argThat(wd -> {
           assertEquals("uk", wd.getCountry());
           assertEquals("London", wd.getCity());
           assertEquals("12345", wd.getApiKey());
           assertNotNull(wd.getCreatedAt());
           assertEquals(json, wd.getRawResponse());

           return true;
       }));
    }

    @Test
    public void testGetLatestWeatherData_happyPath_succeeds() throws Exception {
        when(weatherDataRepository.findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(anyString(), anyString(),anyString()))
                .thenReturn(createWeatherData("valid"));

        WeatherResponse weatherResponse = weatherDataService.getLatestWeatherData("uk", "London", "12345", WeatherResponse.class);
        assertEquals("light intensity drizzle", weatherResponse.getWeatherDescription());
    }

    @Test
    public void testGetLatestWeatherData_missingData_succeeds() throws Exception {
        when(weatherDataRepository.findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(anyString(), anyString(),anyString()))
                .thenReturn(createWeatherData("partial"));

        WeatherResponse weatherResponse = weatherDataService.getLatestWeatherData("uk", "London", "12345", WeatherResponse.class);
        assertNull(weatherResponse.getWeatherDescription());
    }

    @Test
    public void testGetLatestWeatherData_malformedJson_fails() throws Exception {
        when(weatherDataRepository.findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(anyString(), anyString(),anyString()))
                .thenReturn(createWeatherData("invalid"));

        try {
            weatherDataService.getLatestWeatherData("uk", "London", "12345", WeatherResponse.class);
            fail("Should not get here!");
        } catch (InvalidJsonException e) {
            // OK
        }
    }

    @Test
    public void testGetLatestWeatherData_malformedJsonPath_fails() throws Exception {
        ReflectionTestUtils.setField(weatherDataService, "fieldsMapping",
                Map.of("weatherDescription", "----%^(*"));
        when(weatherDataRepository.findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(anyString(), anyString(), anyString()))
                .thenReturn(createWeatherData("valid"));

        try {
            weatherDataService.getLatestWeatherData("uk", "London", "12345", WeatherResponse.class);
            fail("Should not get here!");
        } catch (InvalidPathException e) {
            // OK
        }
    }


    private WeatherData createWeatherData(String jsonFilename) throws IOException {
        WeatherData weatherData = new WeatherData();
        weatherData.setRawResponse(readJson(jsonFilename));
        return weatherData;
    }

    private String readJson(String filename) throws IOException {
        return new String(resourceLoader.getResource("/responses/" + filename + ".json")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
