package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the payment distribution status of a corporate action event.
 */
public enum DividendStatus {
    @JsonProperty("not_approved")
    NOT_APPROVED,

    @JsonProperty("approved")
    APPROVED,

    @JsonProperty("paid")
    PAID,

    /**
     * Safe fallback for unmapped types.
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
