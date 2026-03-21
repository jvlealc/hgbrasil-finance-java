package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single corporate action event in the historical series.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendSeries(
        DividendType type,
        DividendCategory category,
        BigDecimal amount,
        @JsonProperty("approval_date")
        LocalDate approvalDate,
        @JsonProperty("com_date")
        LocalDate comDate,
        @JsonProperty("payment_date")
        LocalDate paymentDate,
        DividendStatus status
) {}
