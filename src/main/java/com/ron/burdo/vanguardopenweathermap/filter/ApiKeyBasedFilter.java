package com.ron.burdo.vanguardopenweathermap.filter;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

abstract class ApiKeyBasedFilter {
    private final OpenWeatherMapProperties openWeatherMapProperties;
    private final Set<String> apiKeys;

    protected ApiKeyBasedFilter(OpenWeatherMapProperties openWeatherMapProperties) {
        this.openWeatherMapProperties = openWeatherMapProperties;
        this.apiKeys = new HashSet<>(openWeatherMapProperties.getApiKeys());
    }

    protected OpenWeatherMapProperties getOpenWeatherMapProperties() {
        return openWeatherMapProperties;
    }

    protected String getApiKey(ClientRequest clientRequest) {
       UriComponents uriComponents = UriComponentsBuilder.fromUri(clientRequest.url()).build();
       String apiKey = uriComponents.getQueryParams().getFirst(openWeatherMapProperties.getApiKeyParamName());
       if (!apiKeys.contains(apiKey)) {
           throw new ApiKeyNotFoundException("API Key " + apiKey + " is unknown");
       }

       return apiKey;
   }
}
