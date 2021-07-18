package com.ron.burdo.vanguardopenweathermap.filter;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RateFilter extends ApiKeyBasedFilter implements ExchangeFilterFunction {
    private final Map<String, List<Instant>> hitsMap;

    @Autowired
    public RateFilter(OpenWeatherMapProperties openWeatherMapProperties) {
        super(openWeatherMapProperties);
        hitsMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
        String apiKey = getApiKey(clientRequest);
        int capacity = getOpenWeatherMapProperties().getRequestsPerTimeUnit();
        ChronoUnit chronoUnit = getOpenWeatherMapProperties().getTimeUnit();

        List<Instant> apiHits = hitsMap.computeIfAbsent(apiKey, key -> new LinkedList<>());
        Instant now = Instant.now();
        synchronized (apiHits) {
            if (apiHits.size() == capacity
                && Duration.between(apiHits.get(0), now).compareTo(chronoUnit.getDuration()) >= 0) {
                apiHits.remove(0);
            }
            if (apiHits.size() < capacity) {
                apiHits.add(now);
            } else {
                throw new RateExceededException("Calls rate exceeded for API key " + apiKey, capacity, chronoUnit);
            }
        }

        return exchangeFunction.exchange(clientRequest);
    }

}
