package com.ron.burdo.vanguardopenweathermap.filter;

import java.time.temporal.ChronoUnit;

public class RateExceededException extends RuntimeException {
    private final int limit;
    private final ChronoUnit chronoUnit;

    public RateExceededException(String message, int limit, ChronoUnit chronoUnit) {
        super(message);

        this.limit = limit;
        this.chronoUnit = chronoUnit;
    }

    public int getLimit() {
        return limit;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }
}
