package com.ron.burdo.vanguardopenweathermap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @NotNull
    private HttpStatus httpStatus;

    @NotNull
    private String message;
}
