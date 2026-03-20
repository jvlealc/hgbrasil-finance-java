package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Métricas consolidadas de dividendos dos últimos 12 meses
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendSummary(
        @JsonProperty("yield_12m_percent")
        BigDecimal yield12mPercent,
        @JsonProperty("yield_12m_cash")
        BigDecimal yield12mCash
) {}
