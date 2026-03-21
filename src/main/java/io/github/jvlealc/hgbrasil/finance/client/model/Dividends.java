package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Dividend information within the asset context.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Dividends(
        @JsonProperty("yield_12m")
        BigDecimal yield12m,
        @JsonProperty("yield_12m_sum")
        BigDecimal yield12mSum,
        @JsonProperty("last_payment")
        BigDecimal lastPayment
) {}
