package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single corporate action event in the historical series of stock splits or reverse splits.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitEvent(
        SplitType type,
        @JsonProperty("factor_from")
        BigDecimal factorFrom,
        @JsonProperty("factor_to")
        BigDecimal factorTo,
        BigDecimal ratio,
        @JsonProperty("com_date")
        LocalDate comDate,
        @JsonProperty("effective_date")
        LocalDate effectiveDate,
        SplitStatus status
) {}
