package com.ron.burdo.vanguardopenweathermap.config;


import com.ron.burdo.vanguardopenweathermap.filter.ApiKeyFilter;
import com.ron.burdo.vanguardopenweathermap.filter.RateFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientFactory {

    @Bean
    WebClient webClient(OpenWeatherMapProperties properties,
                        RateFilter rateFilter,
                        ApiKeyFilter apiKeyFilter) {
        return WebClient.builder()
                .uriBuilderFactory(new DefaultUriBuilderFactory(properties.getUriTemplate()))
                .filter(apiKeyFilter.andThen(rateFilter))
                .build();
    }

}
