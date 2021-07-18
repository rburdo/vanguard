package com.ron.burdo.vanguardopenweathermap.config;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "open-weather-map")
@NoArgsConstructor
@Data
@Component
public class OpenWeatherMapProperties {
    private String apiKeyParamName;
    private String uriTemplate;
    private List<String> apiKeys;
    private Integer requestsPerTimeUnit;
    private ChronoUnit timeUnit;
    private Map<String, String> responseMappings;
}
