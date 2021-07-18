package com.ron.burdo.vanguardopenweathermap.controller;

import com.ron.burdo.vanguardopenweathermap.filter.ApiKeyNotFoundException;
import com.ron.burdo.vanguardopenweathermap.filter.RateExceededException;
import com.ron.burdo.vanguardopenweathermap.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = { ApiKeyNotFoundException.class, ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse apiKeyNotFoundException(Exception ex) {
        log.error("", ex);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(value = { RateExceededException.class })
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse rateExceededException(RateExceededException ex) {
        log.error("", ex);
        return new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(value = { NoResultException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse noResultException(NoResultException ex) {
        log.error("", ex);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = { WebClientResponseException.class } )
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse webClientResponseException(WebClientResponseException ex) {
        log.error("", ex);
        return new ErrorResponse(ex.getStatusCode(),
                        "Calling OpenWeatherMap service failed: " + ex.getMessage());
    }

}