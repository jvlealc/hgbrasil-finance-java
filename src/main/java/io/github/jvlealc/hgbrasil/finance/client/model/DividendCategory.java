package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum DividendCategory {
    @JsonProperty("cash")
    CASH,

    @JsonProperty("stock")
    STOCK,

    @JsonEnumDefaultValue
    UNKNOWN
}
