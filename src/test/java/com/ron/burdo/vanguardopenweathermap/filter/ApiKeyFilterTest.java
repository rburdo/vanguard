package com.ron.burdo.vanguardopenweathermap.filter;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ApiKeyFilterTest {
    private ApiKeyFilter apiKeyFilter;
    private ClientRequest clientRequest;
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    public void setup() {
        OpenWeatherMapProperties properties = mock(OpenWeatherMapProperties.class);
        when(properties.getApiKeys()).thenReturn(List.of("ABCDE", "12345"));
        when(properties.getApiKeyParamName()).thenReturn("apiKey");

        clientRequest = mock(ClientRequest.class);
        exchangeFunction = mock(ExchangeFunction.class);

        apiKeyFilter = new ApiKeyFilter(properties);
    }

    @Test
    public void testValidApiKey() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        apiKeyFilter.filter(clientRequest, exchangeFunction);
        verify(exchangeFunction).exchange(clientRequest);
    }

    @Test
    public void testInvalidApiKey() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=87654"));

        try {
            apiKeyFilter.filter(clientRequest, exchangeFunction);
            fail("Should not get here!");
        } catch (ApiKeyNotFoundException e) {
            assertEquals("API Key 87654 is unknown", e.getMessage());
        }

    }
}
