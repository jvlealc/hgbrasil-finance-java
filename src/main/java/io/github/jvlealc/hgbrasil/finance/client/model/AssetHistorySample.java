package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Represents a single time-series data point (OHLCV) in an asset's history.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetHistorySample(
        OffsetDateTime date,
        BigDecimal open,
        BigDecimal close,
        BigDecimal high,
        BigDecimal low,
        BigDecimal volume
) {}
