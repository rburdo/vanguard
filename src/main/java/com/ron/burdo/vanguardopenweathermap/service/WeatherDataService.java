package com.ron.burdo.vanguardopenweathermap.service;

import com.ron.burdo.vanguardopenweathermap.model.WeatherResponse;

public interface WeatherDataService {

    void addWeatherData(String country, String city, String apiKey, String rawResponse);

    <T> T getLatestWeatherData(String country, String city, String apiKey, Class<T> clazz);

}
