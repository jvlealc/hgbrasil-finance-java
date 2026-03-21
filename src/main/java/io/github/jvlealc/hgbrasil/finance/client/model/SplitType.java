package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type of corporate action event that affects share quantity.
 * Indicates whether a stock split or reverse split occurred.
 * */
public enum SplitType {
    @JsonProperty("split")
    SPLIT,

    @JsonProperty("reverse_split")
    REVERSE_SPLIT,

    /**
     * Safe fallback for unmapped types.
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
