package com.ron.burdo.vanguardopenweathermap.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "WEATHER_DATA", indexes = { @Index(name = "WEATHER_DATA_INDEX", columnList = "API_KEY,COUNTRY,CITY") })
@Data
@NoArgsConstructor
public class WeatherData {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "API_KEY", nullable = false, updatable = false)
    private String apiKey;

    @Column(name = "COUNTRY", nullable = false, updatable = false)
    private String country;

    @Column(name = "CITY", nullable = false, updatable = false)
    private String city;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Date createdAt;

    @Lob
    @Column(name = "RAW_RESPONSE", nullable = false, updatable = false)
    private String rawResponse;
}
