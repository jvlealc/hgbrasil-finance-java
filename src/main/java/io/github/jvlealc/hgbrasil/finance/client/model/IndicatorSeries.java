package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single event in the historical series of the economic indicator.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndicatorSeries(
        String period,
        @JsonProperty("publish_date")
        LocalDate publishDate,
        BigDecimal value
) {}
