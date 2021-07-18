package com.ron.burdo.vanguardopenweathermap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import com.ron.burdo.vanguardopenweathermap.jpa.WeatherData;
import com.ron.burdo.vanguardopenweathermap.jpa.WeatherDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WeatherDataServiceImpl implements WeatherDataService {
    private final WeatherDataRepository repository;
    private final Map<String, String> fieldsMapping;
    private final ObjectMapper objectMapper;

    @Autowired
    public WeatherDataServiceImpl(WeatherDataRepository repository, OpenWeatherMapProperties openWeatherMapProperties) {
        this.repository = repository;
        this.fieldsMapping = openWeatherMapProperties.getResponseMappings();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void addWeatherData(String country, String city, String apiKey, String rawResponse) {
        WeatherData weatherData = new WeatherData();
        weatherData.setApiKey(apiKey);
        weatherData.setCountry(country);
        weatherData.setCity(city);
        weatherData.setCreatedAt(Date.from(Instant.now()));
        weatherData.setRawResponse(rawResponse);
        repository.save(weatherData);
    }

    @Override
    public <T> T getLatestWeatherData(String country, String city, String apiKey, Class<T> clazz) {
       WeatherData weatherData = repository.findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(country, city, apiKey);
       if (weatherData == null) {
            throw new NoResultException(String.format("No data exists for apiKey = %s, country = %s, city = %s",
                    apiKey, country, city));
       }

       Map<String, Object> responseMap = new HashMap<>();
       DocumentContext jsonContext = JsonPath.parse(weatherData.getRawResponse());
       fieldsMapping.forEach((mappingKey, jsonPath) -> {
           try {
               responseMap.put(mappingKey, jsonContext.read(jsonPath));
           } catch (PathNotFoundException e) {
               log.debug(jsonPath + " could not be found");
               responseMap.put(mappingKey, null);
           }
       });
       return objectMapper.convertValue(responseMap, clazz);
    }

}
