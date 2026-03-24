package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Resume with accumulated values of indicator for the last 12 month.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndicatorSummary(
        BigDecimal ytd,
        @JsonProperty("last_12m")
        BigDecimal last12m
) {}
