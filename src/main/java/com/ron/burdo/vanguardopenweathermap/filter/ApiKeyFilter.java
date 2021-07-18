package com.ron.burdo.vanguardopenweathermap.filter;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyFilter extends ApiKeyBasedFilter implements ExchangeFilterFunction {

    @Autowired
    public ApiKeyFilter(OpenWeatherMapProperties openWeatherMapProperties) {
        super(openWeatherMapProperties);
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
        getApiKey(clientRequest);

        return exchangeFunction.exchange(clientRequest);
    }
}
