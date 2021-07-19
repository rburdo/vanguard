package com.ron.burdo.vanguardopenweathermap.controller;


import com.ron.burdo.vanguardopenweathermap.model.WeatherResponse;
import com.ron.burdo.vanguardopenweathermap.service.WeatherDataService;
import com.ron.burdo.vanguardopenweathermap.validation.CountryCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/")
public class OpenWeatherMapController {

    private final WebClient webClient;

    private final WeatherDataService weatherDataService;

    @Autowired
    public OpenWeatherMapController(WebClient webClient,
                                    WeatherDataService weatherDataService) {
        this.webClient = webClient;
        this.weatherDataService = weatherDataService;
    }

    @GetMapping(path = "/{country}/{city}")
    public WeatherResponse getWeather(@PathVariable @NotBlank @Size(max = 2) @CountryCode String country,
                                      @PathVariable @NotBlank String city,
                                      @RequestHeader(name = "api-key") @NotBlank String apiKey) {
        log.debug("Calling OpenWeatherMap for country = {}, city = {}",  country, city);

        webClient
            .get()
            .uri(uriBuilder ->
                    uriBuilder.build(Map.of("country", country, "city", city, "apiKey", apiKey)))
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(rawResponse -> weatherDataService.addWeatherData(country, city, apiKey, rawResponse))
            .block();

        // Actually redundant - we already have the response so no need to read it from the database.
        // However, querying the database is one of the task's requirements.
        WeatherResponse weatherResponse =
                weatherDataService.getLatestWeatherData(country, city, apiKey, WeatherResponse.class);

        log.debug("Weather received successfully for country = {}, city = {}. Weather is {}",
                country, city, weatherResponse.getWeatherDescription());
        return weatherResponse;
    }

}
