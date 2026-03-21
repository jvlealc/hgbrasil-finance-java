package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single Ibovespa intraday data point
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IbovespaIntradayPoint(
        BigDecimal points,
        BigDecimal change,
        @JsonFormat(pattern = "yyyyMMddHHmmss")
        LocalDateTime date
) {}
