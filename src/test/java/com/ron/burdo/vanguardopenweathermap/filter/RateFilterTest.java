package com.ron.burdo.vanguardopenweathermap.filter;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class RateFilterTest {
    private RateFilter rateFilter;
    private ClientRequest clientRequest;
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    public void setup() {
        OpenWeatherMapProperties properties = mock(OpenWeatherMapProperties.class);
        when(properties.getApiKeys()).thenReturn(List.of("ABCDE", "12345"));
        when(properties.getApiKeyParamName()).thenReturn("apiKey");
        when(properties.getRequestsPerTimeUnit()).thenReturn(3);
        when(properties.getTimeUnit()).thenReturn(ChronoUnit.SECONDS);

        clientRequest = mock(ClientRequest.class);
        exchangeFunction = mock(ExchangeFunction.class);

        rateFilter = new RateFilter(properties);
    }

    @Test
    @Timeout(1)
    public void testThreeCallsPerSecondSameApi_succeeds() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);
        verify(exchangeFunction, times(3)).exchange(clientRequest);
    }

    @Test
    @Timeout(1)
    public void testThreeCallsPerSecondNotSameApi_succeeds() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);

        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=ABCDE"));
        rateFilter.filter(clientRequest, exchangeFunction);

        verify(exchangeFunction, times(3)).exchange(clientRequest);
    }

    @Test
    @Timeout(1)
    public void testFourCallsPerSecondSameApi_fails() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);

        try {
            rateFilter.filter(clientRequest, exchangeFunction);
            fail("Should not get here!");
        } catch (RateExceededException e) {
            assertEquals("Calls rate exceeded for API key 12345", e.getMessage());
            assertEquals(3, e.getLimit());
            assertEquals(ChronoUnit.SECONDS, e.getChronoUnit());
        }
    }


    @Test
    @Timeout(2)
    public void testFourCallsPerTwoSecondsSameApi_succeeds() throws Exception {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);

        Thread.sleep(1000L);

        rateFilter.filter(clientRequest, exchangeFunction);
        verify(exchangeFunction, times(4)).exchange(clientRequest);
    }


    @Test
    @Timeout(1)
    public void testFourCallsPerSecondNotSameApi_succeeds() {
        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=12345"));

        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);
        rateFilter.filter(clientRequest, exchangeFunction);

        when(clientRequest.url()).thenReturn(URI.create("https://host.com.au/data?q=London,uk&apiKey=ABCDE"));
        rateFilter.filter(clientRequest, exchangeFunction);

        verify(exchangeFunction, times(4)).exchange(clientRequest);
    }
}
