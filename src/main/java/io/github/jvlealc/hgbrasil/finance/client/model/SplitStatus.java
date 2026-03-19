package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa o status de eventos corporativos de grupamentos ou desdobramentos.
 */
public enum SplitStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("confirmed")
    CONFIRMED,

    @JsonEnumDefaultValue
    UNKNOWN
}
