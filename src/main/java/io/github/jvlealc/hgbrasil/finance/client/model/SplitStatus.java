package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the status of corporate action event related to splits or reverse splits.
 */
public enum SplitStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("confirmed")
    CONFIRMED,

    /**
     * Safe fallback for unmapped types.
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
