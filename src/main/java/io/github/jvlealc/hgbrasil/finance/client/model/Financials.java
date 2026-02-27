package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Informações de valor patrimonial
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Financials(
        Long equity,
        @JsonProperty("quota_count")
        Long quotaCount,
        @JsonProperty("equity_per_share")
        BigDecimal equityPerShare,
        @JsonProperty("price_to_book_ratio")
        BigDecimal priceToBookRatio,
        Dividends dividends
) {}
