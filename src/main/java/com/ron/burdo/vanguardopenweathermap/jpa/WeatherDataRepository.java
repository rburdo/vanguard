package com.ron.burdo.vanguardopenweathermap.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    WeatherData findTopByCountryAndCityAndApiKeyOrderByCreatedAtDesc(String country, String city, String apiKey);

}
