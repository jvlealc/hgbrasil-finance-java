package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the periodicity of the histórical series of an economic indicator.
 * */
public enum IndicatorPeriodicity {
    @JsonProperty("daily")
    DAILY,

    @JsonProperty("monthly")
    MONTHLY,

    /**
     * Safe fallback for unmapped types.
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
