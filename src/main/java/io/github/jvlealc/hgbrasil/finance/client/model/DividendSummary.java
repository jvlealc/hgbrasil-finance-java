package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Consolidated dividend metrics for the last 12 months.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendSummary(
        @JsonProperty("yield_12m_percent")
        BigDecimal yield12mPercent,
        @JsonProperty("yield_12m_cash")
        BigDecimal yield12mCash
) {}
