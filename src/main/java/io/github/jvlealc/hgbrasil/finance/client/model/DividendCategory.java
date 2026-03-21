package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents how a corporate action is distributed.
 * Indicates whether the payment is made in cash or in assets (shares).
 */
public enum DividendCategory {
    @JsonProperty("cash")
    CASH,

    @JsonProperty("stock")
    STOCK,

    /**
     * Safe fallback for unmapped types.
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
